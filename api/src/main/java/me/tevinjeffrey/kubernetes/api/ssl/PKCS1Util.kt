package me.tevinjeffrey.kubernetes.api.ssl


import java.io.*
import java.math.BigInteger
import java.security.spec.RSAPrivateCrtKeySpec

/**
 * This code is inspired and taken over from net.auth.core:oauth
 * (albeit in a highly stripped variation):
 *
 *
 * Source is from http://oauth.googlecode.com/svn/code/java/ which is licensed
 * under the APL (http://oauth.googlecode.com/svn/code/java/LICENSE.txt)
 *
 *
 * All credits go to the original author (zhang)
 *
 * @author roland
 * @since 30/09/15
 */
internal object PKCS1Util {

  @Throws(IOException::class)
  fun decodePKCS1(keyBytes: ByteArray): RSAPrivateCrtKeySpec {
    var parser = DerParser(keyBytes)
    val sequence = parser.read()
    sequence.validateSequence()
    parser = DerParser(sequence.value)
    parser.read()

    return RSAPrivateCrtKeySpec(next(parser), next(parser),
        next(parser), next(parser),
        next(parser), next(parser),
        next(parser), next(parser))
  }

  // ==========================================================================================

  @Throws(IOException::class)
  private fun next(parser: DerParser): BigInteger {
    return parser.read().integer
  }

  internal class DerParser @Throws(IOException::class)
  constructor(bytes: ByteArray) {

    private val `in`: InputStream

    private val length: Int
      @Throws(IOException::class)
      get() {
        val i = `in`.read()
        if (i == -1) {
          throw IOException("Invalid DER: length missing")
        }

        if (i and 0x7F.inv() == 0) {
          return i
        }

        val num = i and 0x7F
        if (i >= 0xFF || num > 4) {
          throw IOException("Invalid DER: length field too big ("
              + i + ")")
        }

        val bytes = ByteArray(num)
        if (`in`.read(bytes) < num) {
          throw IOException("Invalid DER: length too short")
        }

        return BigInteger(1, bytes).toInt()
      }

    init {
      this.`in` = ByteArrayInputStream(bytes)
    }

    @Throws(IOException::class)
    fun read(): Asn1Object {
      val tag = `in`.read()

      if (tag == -1) {
        throw IOException("Invalid DER: stream too short, missing tag")
      }

      val length = length
      val value = ByteArray(length)
      if (`in`.read(value) < length) {
        throw IOException("Invalid DER: stream too short, missing value")
      }

      return Asn1Object(tag, value)
    }
  }

  internal class Asn1Object(private val tag: Int, val value: ByteArray) {

    private val type: Int

    //$NON-NLS-1$
    val integer: BigInteger
      @Throws(IOException::class)
      get() {
        if (type != 0x02) {
          throw IOException("Invalid DER: object is not integer")
        }
        return BigInteger(value)
      }

    init {
      this.type = tag and 0x1F
    }

    @Throws(IOException::class)
    fun validateSequence() {
      if (type != 0x10) {
        throw IOException("Invalid DER: not a sequence")
      }
      if (tag and 0x20 != 0x20) {
        throw IOException("Invalid DER: can't parse primitive entity")
      }
    }
  }
}
