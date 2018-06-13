package me.tevinjeffrey.kubernetes.api.ssl

import io.fabric8.kubernetes.client.Config
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.utils.HttpClientUtils
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.inject.Inject

class SSLUtils @Inject constructor(private val certUtils: CertUtils) {

  private val LOG = LoggerFactory.getLogger(SSLUtils::class.java)

  fun isHttpsAvailable(config: Config): Boolean {
    val sslConfig = ConfigBuilder(config)
        .withMasterUrl(Config.HTTPS_PROTOCOL_PREFIX + config.masterUrl)
        .withRequestTimeout(1000)
        .withConnectionTimeout(1000)
        .build()

    val client = HttpClientUtils.createHttpClient(config)
    try {
      val request = Request.Builder().get().url(sslConfig.masterUrl)
          .build()
      val response = client!!.newCall(request).execute()
      response.body()!!.use { body -> return response.isSuccessful }
    } catch (t: Throwable) {
      LOG.warn("SSL handshake failed. Falling back to insecure connection.")
    } finally {
      if (client != null && client.connectionPool() != null) {
        client.connectionPool().evictAll()
      }
    }
    return false
  }

  fun sslContext(keyManagers: Array<KeyManager>?, trustManagers: Array<TrustManager>): SSLContext {
    val sslContext = SSLContext.getInstance("TLSv1.2")
    sslContext.init(keyManagers, trustManagers, SecureRandom())
    return sslContext
  }

  fun trustManagers(config: Config): Array<TrustManager> {
    return trustManagers(config.caCertData, config.caCertFile, config.isTrustCerts, config.keyStorePassphrase)
  }

  fun trustManagers(certData: String?, certFile: String?, isTrustCerts: Boolean?, storePassphrase: String): Array<TrustManager> {
    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    var trustStore: KeyStore? = null
    if (isTrustCerts!!) {
      return arrayOf(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, s: String) {}

        override fun checkServerTrusted(chain: Array<X509Certificate>, s: String) {}

        override fun getAcceptedIssuers(): Array<X509Certificate> {
          return emptyArray()
        }
      })
    } else if (!certData.isNullOrEmpty() || !certFile.isNullOrEmpty()) {
      trustStore = certUtils.createTrustStore(certData, certFile, storePassphrase.toCharArray())
    }
    tmf.init(trustStore)
    return tmf.trustManagers
  }

  fun keyManagers(config: Config): Array<KeyManager>? {
    return keyManagers(config.clientCertData, config.clientCertFile, config.clientKeyData, config.clientKeyFile, config.clientKeyAlgo, config.clientKeyPassphrase, "somepassword")
  }

  fun keyManagers(certData: String?, certFile: String?, keyData: String?, keyFile: String?, algo: String, passphrase: String?, storePassphrase: String): Array<KeyManager>? {
    var keyManagers: Array<KeyManager>? = null
    if ((!certData.isNullOrEmpty() || !certFile.isNullOrEmpty()) && (!keyData.isNullOrEmpty() || keyFile.isNullOrEmpty())) {
      val keyStore = certUtils.createKeyStore(certData, certFile, keyData, keyFile, algo, passphrase, storePassphrase.toCharArray())
      val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
      kmf.init(keyStore, passphrase?.toCharArray())
      keyManagers = kmf.keyManagers
    }
    return keyManagers
  }
}
