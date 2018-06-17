package me.tevinjeffrey.kubernetes.api

import android.webkit.URLUtil
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import me.tevinjeffrey.kubernetes.api.ssl.HttpClientUtils
import me.tevinjeffrey.kubernetes.db.Cluster
import okhttp3.OkHttpClient

class KubernetesClientProvider(
    private val okHttpClient: OkHttpClient,
    private val clientUtils: HttpClientUtils
) {
  fun get(config: Cluster): DefaultKubernetesClient {
    val configBuilder = ConfigBuilder()
        .withMasterUrl(config.server)
        .withOauthToken(config.token)
        .withUsername(config.username)
        .withPassword(config.password)
        .withTrustCerts(config.insecureSkipTLSVerify)
        .withClientCertData(config.clientCertificate)
        .withClientKeyData(config.clientKey)
        .withCaCertData(config.certificateAuthority)
        .withKeyStorePassphrase("")
        .withClientKeyAlgo("RSA")

    if (config.shouldProxy && !config.proxyUrl.isNullOrEmpty()) {
      if (URLUtil.isHttpsUrl(config.proxyUrl)) {
        configBuilder.withHttpsProxy(config.proxyUrl)
      } else {
        configBuilder.withHttpProxy(config.proxyUrl)
      }
    }

    val c = configBuilder.build()
    val httpClient = clientUtils.createHttpClient(okHttpClient.newBuilder(), c)

    return DefaultKubernetesClient(httpClient, c)
  }
}