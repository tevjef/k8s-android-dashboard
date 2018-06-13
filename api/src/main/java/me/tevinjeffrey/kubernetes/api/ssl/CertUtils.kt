package me.tevinjeffrey.kubernetes.api.ssl

import android.content.Context
import android.net.Uri
import okio.BufferedSource
import okio.ByteString
import okio.Okio
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.security.KeyFactory
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject

class CertUtils @Inject constructor(val context: Context) {

  private fun getInputStreamFromDataOrFile(data: String?, file: String?): InputStream {
    if (data == null && file == null) {
      throw IllegalStateException("Neither data nor file was provided")
    }

    return when {
      data != null -> {
        val decoded = ByteString.decodeBase64(data)
        val bytes = decoded?.toByteArray() ?: data.toByteArray()

        ByteArrayInputStream(bytes)
      }
      file != null -> {
        val source = Okio.buffer(Okio.source(context.contentResolver.openInputStream(Uri.parse(file))))
        ByteArrayInputStream(source.readByteArray())
      }
      else -> ByteArrayInputStream("".toByteArray())
    }
  }

  fun createTrustStore(caCertData: String?, caCertFile: String?, storePassphrase: CharArray): KeyStore {
    getInputStreamFromDataOrFile(caCertData, caCertFile).use { pemInputStream ->
      return createTrustStore(pemInputStream, storePassphrase)
    }
  }

  fun createTrustStore(pemInputStream: InputStream?, storePassphrase: CharArray): KeyStore {
    val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())

    trustStore.load(null, storePassphrase)

    val certificateFactory = CertificateFactory.getInstance("X.509")
    val certificates = certificateFactory.generateCertificates(pemInputStream)
    if (certificates.isEmpty()) {
      throw IllegalArgumentException("expected non-empty set of trusted certificates")
    }

    for (certificate in certificates) {
      val certificateAlias = (certificate as X509Certificate).subjectX500Principal.name
      trustStore.setCertificateEntry(certificateAlias, certificate)
    }

    return trustStore
  }

  fun createKeyStore(certInputStream: InputStream?, keyInputStream: InputStream?, clientKeyAlgo: String, clientKeyPassphrase: CharArray?, storePassphrase: CharArray): KeyStore {
    val certFactory = CertificateFactory.getInstance("X.509")
    val cert = certFactory.generateCertificate(certInputStream) as X509Certificate

    val keyBytes = decodePem(keyInputStream)

    val keyFactory = KeyFactory.getInstance(clientKeyAlgo)
    val privateKey = try {
      keyFactory.generatePrivate(PKCS8EncodedKeySpec(keyBytes))
    } catch (e: InvalidKeySpecException) {
      val keySpec = PKCS1Util.decodePKCS1(keyBytes)
      keyFactory.generatePrivate(keySpec)
    }

    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, storePassphrase)

    val alias = cert.subjectX500Principal.name
    keyStore.setKeyEntry(alias, privateKey, clientKeyPassphrase, arrayOf<Certificate>(cert))

    return keyStore
  }

  fun createKeyStore(clientCertData: String?, clientCertFile: String?, clientKeyData: String?,
                     clientKeyFile: String?, clientKeyAlgo: String, clientKeyPassphrase: String?, storePassphrase: CharArray): KeyStore {
    getInputStreamFromDataOrFile(clientCertData, clientCertFile).use { certInputStream ->
      getInputStreamFromDataOrFile(clientKeyData, clientKeyFile).use { keyInputStream ->
        return createKeyStore(certInputStream, keyInputStream, clientKeyAlgo, clientKeyPassphrase?.toCharArray(), storePassphrase)
      }
    }
  }

  // This method is inspired and partly taken over from
  // http://oauth.googlecode.com/svn/code/java/
  // All credits to belong to them.
  private fun decodePem(keyInputStream: InputStream?): ByteArray {
    val bufferedSource = Okio.buffer(Okio.source(keyInputStream))
    bufferedSource.use { source ->
      val line = source.readUtf8Line()
      if (!line.isNullOrEmpty() && line.orEmpty().contains("-----BEGIN ")) {
        return readBytes(source, line.orEmpty().trim { it <= ' ' }.replace("BEGIN", "END"))
      }
      throw IOException("PEM is invalid: no begin marker")
    }
  }

  @Throws(IOException::class)
  private fun readBytes(source: BufferedSource, endMarker: String): ByteArray {
    val buf = StringBuffer()

    while (true) {
      val line = source.readUtf8Line() ?: break

      if (line.indexOf(endMarker) != -1) {
        return ByteString.decodeBase64(buf.toString())!!.toByteArray()
      }
      buf.append(line.trim { it <= ' ' })
    }

    throw IOException("PEM is invalid : No end marker")
  }
}
