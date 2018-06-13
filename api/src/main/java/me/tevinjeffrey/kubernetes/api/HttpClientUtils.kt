package me.tevinjeffrey.kubernetes.api


import io.fabric8.kubernetes.client.Config
import io.fabric8.kubernetes.client.KubernetesClientException
import io.fabric8.kubernetes.client.utils.ImpersonatorInterceptor
import io.fabric8.kubernetes.client.utils.IpAddressMatcher
import io.fabric8.kubernetes.client.utils.Utils.isNotNullOrEmpty
import okhttp3.*
import okhttp3.ConnectionSpec.CLEARTEXT
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.MalformedURLException
import java.net.Proxy
import java.net.URL
import java.security.GeneralSecurityException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object HttpClientUtils {

  private var VALID_IPV4_PATTERN: Pattern? = null
  val ipv4Pattern = "(http:\\/\\/|https:\\/\\/)?(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])(\\/[0-9]\\d|1[0-9]\\d|2[0-9]\\d|3[0-2]\\d)?"

  init {
    try {
      VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE)
    } catch (e: PatternSyntaxException) {
      throw KubernetesClientException.launderThrowable("Unable to compile ipv4address pattern.", e)
    }

  }

  fun createHttpClient(config: Config): OkHttpClient {
    try {
      val httpClientBuilder = OkHttpClient.Builder()

      // Follow any redirects
      httpClientBuilder.followRedirects(true)
      httpClientBuilder.followSslRedirects(true)

      if (config.isTrustCerts) {
        httpClientBuilder.hostnameVerifier { s, sslSession -> true }
      }

      val trustManagers = SSLUtils.trustManagers(config)
      val keyManagers = SSLUtils.keyManagers(config)

      if (keyManagers != null || trustManagers != null || config.isTrustCerts) {
        var trustManager: X509TrustManager? = null
        if (trustManagers != null && trustManagers.size == 1) {
          trustManager = trustManagers[0] as X509TrustManager
        }

        try {
          val sslContext = SSLUtils.sslContext(keyManagers, trustManagers, config.isTrustCerts)
          httpClientBuilder.sslSocketFactory(sslContext.socketFactory, trustManager!!)
        } catch (e: GeneralSecurityException) {
          throw AssertionError() // The system has no TLS. Just give up.
        }

      } else {
        val context = SSLContext.getInstance("TLSv1.2")
        context.init(keyManagers, trustManagers, null)
        httpClientBuilder.sslSocketFactory(context.socketFactory, trustManagers!![0] as X509TrustManager)
      }

      httpClientBuilder.addInterceptor(Interceptor { chain ->
        val request = chain.request()
        if (isNotNullOrEmpty(config.username) && isNotNullOrEmpty(config.password)) {
          val authReq = chain.request().newBuilder().addHeader("Authorization", Credentials.basic(config.username, config.password)).build()
          return@Interceptor chain.proceed(authReq)
        } else if (isNotNullOrEmpty(config.oauthToken)) {
          val authReq = chain.request().newBuilder().addHeader("Authorization", "Bearer " + config.oauthToken).build()
          return@Interceptor chain.proceed(authReq)
        }
        chain.proceed(request)
      }).addInterceptor(ImpersonatorInterceptor(config))

      val reqLogger = LoggerFactory.getLogger(HttpLoggingInterceptor::class.java)
      if (reqLogger.isTraceEnabled) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClientBuilder.addNetworkInterceptor(loggingInterceptor)
      }

      if (config.connectionTimeout > 0) {
        httpClientBuilder.connectTimeout(config.connectionTimeout.toLong(), TimeUnit.MILLISECONDS)
      }

      if (config.requestTimeout > 0) {
        httpClientBuilder.readTimeout(config.requestTimeout.toLong(), TimeUnit.MILLISECONDS)
      }

      if (config.websocketPingInterval > 0) {
        httpClientBuilder.pingInterval(config.websocketPingInterval, TimeUnit.MILLISECONDS)
      }

      if (config.maxConcurrentRequestsPerHost > 0) {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = config.maxConcurrentRequests
        dispatcher.maxRequestsPerHost = config.maxConcurrentRequestsPerHost
        httpClientBuilder.dispatcher(dispatcher)
      }

      // Only check proxy if it's a full URL with protocol
      if (config.masterUrl.toLowerCase().startsWith(Config.HTTP_PROTOCOL_PREFIX) || config.masterUrl.startsWith(Config.HTTPS_PROTOCOL_PREFIX)) {
        try {
          val proxyUrl = getProxyUrl(config)
          if (proxyUrl != null) {
            httpClientBuilder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyUrl.host, proxyUrl.port)))

            if (config.proxyUsername != null) {
              httpClientBuilder.proxyAuthenticator { route, response ->
                val credential = Credentials.basic(config.proxyUsername, config.proxyPassword)
                response.request().newBuilder().header("Proxy-Authorization", credential).build()
              }
            }
          }

        } catch (e: MalformedURLException) {
          throw KubernetesClientException("Invalid proxy server configuration", e)
        }

      }

      if (config.userAgent != null && !config.userAgent.isEmpty()) {
        httpClientBuilder.addNetworkInterceptor { chain ->
          val agent = chain.request().newBuilder().header("User-Agent", config.userAgent).build()
          chain.proceed(agent)
        }
      }

      if (config.tlsVersions != null && config.tlsVersions.size > 0) {
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(*config.tlsVersions)
            .build()
        httpClientBuilder.connectionSpecs(Arrays.asList(spec, CLEARTEXT))
      }

      return httpClientBuilder.build()
    } catch (e: Exception) {
      throw KubernetesClientException.launderThrowable(e)
    }

  }

  @Throws(MalformedURLException::class)
  private fun getProxyUrl(config: Config): URL? {
    val master = URL(config.masterUrl)
    val host = master.host
    if (config.noProxy != null) {
      for (noProxy in config.noProxy) {
        if (isIpAddress(noProxy)) {
          if (IpAddressMatcher(noProxy).matches(host)) {
            return null
          }
        } else {
          if (host.contains(noProxy)) {
            return null
          }
        }
      }
    }
    var proxy: String? = config.httpsProxy
    if (master.protocol == "http") {
      proxy = config.httpProxy
    }
    return if (proxy != null) {
      URL(proxy)
    } else null
  }

  private fun isIpAddress(ipAddress: String): Boolean {
    val ipMatcher = VALID_IPV4_PATTERN!!.matcher(ipAddress)
    return ipMatcher.matches()
  }
}
