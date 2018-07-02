package me.tevinjeffrey.kubernetes.api.ssl


import io.fabric8.kubernetes.client.Config
import io.fabric8.kubernetes.client.KubernetesClientException
import io.fabric8.kubernetes.client.utils.ImpersonatorInterceptor
import io.fabric8.kubernetes.client.utils.IpAddressMatcher
import io.fabric8.kubernetes.client.utils.Utils.isNotNullOrEmpty
import okhttp3.*
import okhttp3.ConnectionSpec.CLEARTEXT
import okhttp3.logging.HttpLoggingInterceptor
import java.net.InetSocketAddress
import java.net.MalformedURLException
import java.net.Proxy
import java.net.URL
import java.security.GeneralSecurityException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class HttpClientUtils @Inject constructor(private val sslUtils: SSLUtils) {

  private var VALID_IPV4_PATTERN: Pattern? = null
  val ipv4Pattern = "(http:\\/\\/|https:\\/\\/)?(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])(\\/[0-9]\\d|1[0-9]\\d|2[0-9]\\d|3[0-2]\\d)?"

  init {
    try {
      VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE)
    } catch (e: PatternSyntaxException) {
      throw KubernetesClientException.launderThrowable("Unable to compile ipv4address pattern.", e)
    }

  }

  fun createHttpClient(clientBuilder: OkHttpClient.Builder, config: Config): OkHttpClient {
    try {
      // Follow any redirects
      clientBuilder.followRedirects(true)
      clientBuilder.followSslRedirects(true)

      if (config.isTrustCerts) {
        clientBuilder.hostnameVerifier { _, _ -> true }
      }

      val trustManagers = sslUtils.trustManagers(config)
      val keyManagers = sslUtils.keyManagers(config)

      if (keyManagers != null || config.isTrustCerts) {
        var trustManager: X509TrustManager? = null
        if (trustManagers.size == 1) {
          trustManager = trustManagers[0] as X509TrustManager
        }

        try {
          val sslContext = sslUtils.sslContext(keyManagers, trustManagers)
          clientBuilder.sslSocketFactory(sslContext.socketFactory, trustManager!!)
        } catch (e: GeneralSecurityException) {
          throw AssertionError("The system has no TLS. Just give up.")
        }

      } else {
        val context = SSLContext.getInstance("TLSv1.2")
        context.init(keyManagers, trustManagers, null)
        clientBuilder.sslSocketFactory(context.socketFactory, trustManagers[0] as X509TrustManager)
      }

      clientBuilder.addInterceptor(Interceptor { chain ->
        val request = chain.request()
        if (isNotNullOrEmpty(config.username) && isNotNullOrEmpty(config.password)) {
          val authReq = chain
              .request()
              .newBuilder()
              .addHeader("Authorization", Credentials.basic(config.username, config.password))
              .build()
          return@Interceptor chain.proceed(authReq)
        } else if (isNotNullOrEmpty(config.oauthToken)) {
          val authReq = chain
              .request()
              .newBuilder()
              .addHeader("Authorization", "Bearer " + config.oauthToken)
              .build()
          return@Interceptor chain.proceed(authReq)
        }
        chain.proceed(request)
      }).addInterceptor(ImpersonatorInterceptor(config))

//      val loggingInterceptor = HttpLoggingInterceptor()
//      loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//      clientBuilder.addNetworkInterceptor(loggingInterceptor)

      if (config.connectionTimeout > 0) {
        clientBuilder.connectTimeout(config.connectionTimeout.toLong(), TimeUnit.MILLISECONDS)
      }

      if (config.requestTimeout > 0) {
        clientBuilder.readTimeout(config.requestTimeout.toLong(), TimeUnit.MILLISECONDS)
      }

      if (config.websocketPingInterval > 0) {
        clientBuilder.pingInterval(config.websocketPingInterval, TimeUnit.MILLISECONDS)
      }

      if (config.maxConcurrentRequestsPerHost > 0) {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = config.maxConcurrentRequests
        dispatcher.maxRequestsPerHost = config.maxConcurrentRequestsPerHost
        clientBuilder.dispatcher(dispatcher)
      }

      // Only check proxy if it's a full URL with protocol
      if (config.masterUrl.toLowerCase().startsWith(Config.HTTP_PROTOCOL_PREFIX) ||
          config.masterUrl.startsWith(Config.HTTPS_PROTOCOL_PREFIX)) {
        try {
          val proxyUrl = getProxyUrl(config)
          if (proxyUrl != null) {
            clientBuilder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyUrl.host, proxyUrl.port)))
            if (config.proxyUsername != null) {
              clientBuilder.proxyAuthenticator { _, response ->
                val credential = Credentials.basic(config.proxyUsername, config.proxyPassword)
                response
                    .request()
                    .newBuilder()
                    .header("Proxy-Authorization", credential).build()
              }
            }
          }

        } catch (e: MalformedURLException) {
          throw KubernetesClientException("Invalid proxy server configuration", e)
        }

      }

      if (config.userAgent != null && !config.userAgent.isEmpty()) {
        clientBuilder.addNetworkInterceptor { chain ->
          val agent = chain
              .request()
              .newBuilder()
              .header("User-Agent", config.userAgent).build()
          chain.proceed(agent)
        }
      }

      if (config.tlsVersions != null && config.tlsVersions.isNotEmpty()) {
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(*config.tlsVersions)
            .build()
        clientBuilder.connectionSpecs(Arrays.asList(spec, CLEARTEXT))
      }

      return clientBuilder.build()
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
