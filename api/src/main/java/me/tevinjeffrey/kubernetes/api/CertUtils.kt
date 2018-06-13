package me.tevinjeffrey.kubernetes.api

import io.fabric8.kubernetes.client.utils.Utils
import okio.ByteString
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.charset.Charset
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec

object CertUtils {

  private val LOG = LoggerFactory.getLogger(CertUtils::class.java)
  var TRUST_STORE_SYSTEM_PROPERTY = "javax.net.ssl.trustStore"
  var TRUST_STORE_PASSWORD_SYSTEM_PROPERTY = "javax.net.ssl.trustStorePassword"
  var KEY_STORE_SYSTEM_PROPERTY = "javax.net.ssl.keyStore"
  var KEY_STORE_PASSWORD_SYSTEM_PROPERTY = "javax.net.ssl.keyStorePassword"

  private val defaultTrustStoreFile: File
    get() {
      val securityDirectory = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator

      val trustStorePath = System.getProperty(TRUST_STORE_SYSTEM_PROPERTY)
      if (Utils.isNotNullOrEmpty(trustStorePath)) {
        return File(trustStorePath)
      }

      val jssecacertsFile = File(securityDirectory + "jssecacerts")
      return if (jssecacertsFile.exists() && jssecacertsFile.isFile) {
        jssecacertsFile
      } else File(securityDirectory + "cacerts")

    }

  @Throws(IOException::class)
  fun getInputStreamFromDataOrFile(data: String?, file: String?): InputStream? {
    if (data != null) {
      var bytes: ByteArray? = null
      val decoded = ByteString.decodeBase64(data)
      if (decoded != null) {
        bytes = decoded.toByteArray()
      } else {
        bytes = data.toByteArray()
      }

      return ByteArrayInputStream(bytes!!)
    }
    return if (file != null) {
      ByteArrayInputStream(File(file.orEmpty()).readText(Charset.defaultCharset()).trim { it <= ' ' }.toByteArray())
    } else null
  }

  @Throws(IOException::class, CertificateException::class, KeyStoreException::class, NoSuchAlgorithmException::class)
  fun createTrustStore(caCertData: String, caCertFile: String, trustStoreFile: String, trustStorePassphrase: String): KeyStore {
    getInputStreamFromDataOrFile(caCertData, caCertFile)!!.use { pemInputStream -> return createTrustStore(pemInputStream, trustStoreFile, getTrustStorePassphrase(trustStorePassphrase)) }
  }

  private fun getTrustStorePassphrase(trustStorePassphrase: String): CharArray {
    return if (Utils.isNullOrEmpty(trustStorePassphrase)) {
      System.getProperty(TRUST_STORE_PASSWORD_SYSTEM_PROPERTY, "changeit").toCharArray()
    } else trustStorePassphrase.toCharArray()
  }

  @Throws(IOException::class, CertificateException::class, KeyStoreException::class, NoSuchAlgorithmException::class)
  fun createTrustStore(pemInputStream: InputStream?, trustStoreFile: String, trustStorePassphrase: CharArray): KeyStore {
    val trustStore = KeyStore.getInstance("BKS")

    if (Utils.isNotNullOrEmpty(trustStoreFile)) {
      trustStore.load(FileInputStream(trustStoreFile), trustStorePassphrase)
    } else {
      loadDefaultTrustStoreFile(trustStore, trustStorePassphrase)
    }

    while (pemInputStream!!.available() > 0) {
      val certFactory = CertificateFactory.getInstance("X509")
      val cert = certFactory.generateCertificate(pemInputStream) as X509Certificate

      val alias = cert.subjectX500Principal.name
      trustStore.setCertificateEntry(alias, cert)
    }
    return trustStore
  }

  @Throws(IOException::class, CertificateException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class, KeyStoreException::class)
  fun createKeyStore(certInputStream: InputStream?, keyInputStream: InputStream?, clientKeyAlgo: String, clientKeyPassphrase: CharArray, keyStoreFile: String, keyStorePassphrase: CharArray): KeyStore {
    val certFactory = CertificateFactory.getInstance("X509")
    val cert = certFactory.generateCertificate(certInputStream) as X509Certificate

    val keyBytes = decodePem(keyInputStream)

    var privateKey: PrivateKey

    val keyFactory = KeyFactory.getInstance(clientKeyAlgo)
    try {
      // First let's try PKCS8
      privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(keyBytes))
    } catch (e: InvalidKeySpecException) {
      // Otherwise try PKCS8
      val keySpec = PKCS1Util.decodePKCS1(keyBytes)
      privateKey = keyFactory.generatePrivate(keySpec)
    }

    val keyStore = KeyStore.getInstance("BKS")
    if (Utils.isNotNullOrEmpty(keyStoreFile)) {
      keyStore.load(FileInputStream(keyStoreFile), keyStorePassphrase)
    } else {
      loadDefaultKeyStoreFile(keyStore, keyStorePassphrase)
    }

    val alias = cert.subjectX500Principal.name
    keyStore.setKeyEntry(alias, privateKey, clientKeyPassphrase, arrayOf<Certificate>(cert))

    return keyStore
  }

  @Throws(CertificateException::class, NoSuchAlgorithmException::class, IOException::class)
  private fun loadDefaultTrustStoreFile(keyStore: KeyStore, trustStorePassphrase: CharArray) {

    val trustStoreFile = defaultTrustStoreFile

    if (!loadDefaultStoreFile(keyStore, trustStoreFile, trustStorePassphrase)) {
      keyStore.load(null)
    }
  }

  @Throws(CertificateException::class, NoSuchAlgorithmException::class, IOException::class)
  private fun loadDefaultKeyStoreFile(keyStore: KeyStore, keyStorePassphrase: CharArray) {

    val keyStorePath = System.getProperty(KEY_STORE_SYSTEM_PROPERTY)
    if (Utils.isNotNullOrEmpty(keyStorePath)) {
      val keyStoreFile = File(keyStorePath)
      if (loadDefaultStoreFile(keyStore, keyStoreFile, keyStorePassphrase)) {
        return
      }
    }

    keyStore.load(null)
  }

  @Throws(CertificateException::class, NoSuchAlgorithmException::class, IOException::class)
  private fun loadDefaultStoreFile(keyStore: KeyStore, fileToLoad: File, passphrase: CharArray?): Boolean {

    val notLoadedMessage = "There is a problem with reading default keystore/truststore file %s with the passphrase %s " + "- the file won't be loaded. The reason is: %s"

    if (fileToLoad.exists() && fileToLoad.isFile && fileToLoad.length() > 0) {
      try {
        keyStore.load(FileInputStream(fileToLoad), passphrase)
        return true
      } catch (e: Exception) {
        val passphraseToPrint = if (passphrase != null) String(passphrase) else null
        LOG.info(String.format(notLoadedMessage, fileToLoad, passphraseToPrint, e.message))
      }

    }
    return false
  }

  @Throws(IOException::class, CertificateException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class, KeyStoreException::class)
  fun createKeyStore(clientCertData: String, clientCertFile: String, clientKeyData: String,
                     clientKeyFile: String, clientKeyAlgo: String, clientKeyPassphrase: String, keyStoreFile: String,
                     keyStorePassphrase: String): KeyStore {
    getInputStreamFromDataOrFile(clientCertData, clientCertFile)!!.use { certInputStream ->
      getInputStreamFromDataOrFile(clientKeyData, clientKeyFile)!!.use { keyInputStream ->
        return createKeyStore(certInputStream, keyInputStream, clientKeyAlgo, clientKeyPassphrase.toCharArray(),
            keyStoreFile, getKeyStorePassphrase(keyStorePassphrase))
      }
    }
  }

  private fun getKeyStorePassphrase(keyStorePassphrase: String): CharArray {
    return if (Utils.isNullOrEmpty(keyStorePassphrase)) {
      System.getProperty(KEY_STORE_PASSWORD_SYSTEM_PROPERTY, "changeit").toCharArray()
    } else keyStorePassphrase.toCharArray()
  }

  // This method is inspired and partly taken over from
  // http://oauth.googlecode.com/svn/code/java/
  // All credits to belong to them.
  @Throws(IOException::class)
  private fun decodePem(keyInputStream: InputStream?): ByteArray {
    val reader = BufferedReader(InputStreamReader(keyInputStream!!))
    try {
      reader.readLines().forEach { line ->
        if (line.contains("-----BEGIN ")) {
          return readBytes(reader, line.trim { it <= ' ' }.replace("BEGIN", "END"))
        }
      }
      throw IOException("PEM is invalid: no begin marker")
    } finally {
      reader.close()
    }
  }

  @Throws(IOException::class)
  private fun readBytes(reader: BufferedReader, endMarker: String): ByteArray {
    val buf = StringBuffer()

    reader.readLines().forEach { line ->
      if (line.indexOf(endMarker) != -1) {
        return ByteString.decodeBase64(buf.toString())!!.toByteArray()
      }
      buf.append(line.trim { it <= ' ' })
    }
    throw IOException("PEM is invalid : No end marker")
  }
}
