package me.tevinjeffrey.kubernetes.api

import io.fabric8.kubernetes.client.Config
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.utils.HttpClientUtils
import io.fabric8.kubernetes.client.utils.Utils.isNotNullOrEmpty
import me.tevinjeffrey.kubernetes.api.CertUtils.createKeyStore
import me.tevinjeffrey.kubernetes.api.CertUtils.createTrustStore
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.security.spec.InvalidKeySpecException
import javax.net.ssl.*

object SSLUtils {

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

  @Throws(CertificateException::class, UnrecoverableKeyException::class, NoSuchAlgorithmException::class, KeyStoreException::class, IOException::class, InvalidKeySpecException::class, KeyManagementException::class)
  fun sslContext(config: Config): SSLContext {
    return sslContext(keyManagers(config), trustManagers(config), config.isTrustCerts)
  }

  @Throws(CertificateException::class, UnrecoverableKeyException::class, NoSuchAlgorithmException::class, KeyStoreException::class, IOException::class, InvalidKeySpecException::class, KeyManagementException::class)
  fun sslContext(keyManagers: Array<KeyManager>?, trustManagers: Array<TrustManager>, trustCerts: Boolean): SSLContext {
    val sslContext = SSLContext.getInstance("TLSv1.2")
    sslContext.init(keyManagers, trustManagers, SecureRandom())
    return sslContext
  }

  @Throws(CertificateException::class, NoSuchAlgorithmException::class, KeyStoreException::class, IOException::class)
  fun trustManagers(config: Config): Array<TrustManager> {
    return trustManagers(config.caCertData, config.caCertFile, config.isTrustCerts, config.trustStoreFile, config.trustStorePassphrase)
  }

  @Throws(CertificateException::class, NoSuchAlgorithmException::class, KeyStoreException::class, IOException::class)
  fun trustManagers(certData: String, certFile: String, isTrustCerts: Boolean, trustStoreFile: String, trustStorePassphrase: String): Array<TrustManager> {
    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    var trustStore: KeyStore? = null
    if (isTrustCerts) {
      return arrayOf(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, s: String) {}

        override fun checkServerTrusted(chain: Array<X509Certificate>, s: String) {}

        override fun getAcceptedIssuers(): Array<X509Certificate> {
          return emptyArray()
        }
      })
    } else if (isNotNullOrEmpty(certData) || isNotNullOrEmpty(certFile)) {
      trustStore = createTrustStore(certData, certFile, trustStoreFile, trustStorePassphrase)
    }
    tmf.init(trustStore)
    return tmf.trustManagers
  }

  @Throws(NoSuchAlgorithmException::class, UnrecoverableKeyException::class, KeyStoreException::class, CertificateException::class, InvalidKeySpecException::class, IOException::class)
  fun keyManagers(config: Config): Array<KeyManager>? {
    return keyManagers(config.clientCertData, config.clientCertFile, config.clientKeyData, config.clientKeyFile, config.clientKeyAlgo, config.clientKeyPassphrase, config.keyStoreFile, config.keyStorePassphrase)
  }

  @Throws(NoSuchAlgorithmException::class, UnrecoverableKeyException::class, KeyStoreException::class, CertificateException::class, InvalidKeySpecException::class, IOException::class)
  fun keyManagers(certData: String, certFile: String, keyData: String, keyFile: String, algo: String, passphrase: String, keyStoreFile: String, keyStorePassphrase: String): Array<KeyManager>? {
    var keyManagers: Array<KeyManager>? = null
    if ((isNotNullOrEmpty(certData) || isNotNullOrEmpty(certFile)) && (isNotNullOrEmpty(keyData) || isNotNullOrEmpty(keyFile))) {
      val keyStore = createKeyStore(certData, certFile, keyData, keyFile, algo, passphrase, keyStoreFile, keyStorePassphrase)
      val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
      kmf.init(keyStore, passphrase.toCharArray())
      keyManagers = kmf.keyManagers
    }
    return keyManagers
  }

  @Throws(NoSuchAlgorithmException::class, UnrecoverableKeyException::class, KeyStoreException::class, CertificateException::class, InvalidKeySpecException::class, IOException::class)
  fun keyManagers(certInputStream: InputStream, keyInputStream: InputStream, algo: String, passphrase: String, keyStoreFile: String, keyStorePassphrase: String): Array<KeyManager> {
    val keyStore = createKeyStore(certInputStream, keyInputStream, algo, passphrase.toCharArray(), keyStoreFile, keyStorePassphrase.toCharArray())
    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
    kmf.init(keyStore, passphrase.toCharArray())
    return kmf.keyManagers
  }
}
