package me.tevinjeffrey.kubernetes.home.settings.adapter

sealed class CertInfo(val data: String) {
  class X509Info(
      x509Data: String,
      val name: String
  ): CertInfo(x509Data) {
    override fun toString(): String {
      return name
    }
  }

  class KeyInfo(
      keyData: String,
      val format: String,
      val algorithm: String
  ): CertInfo(keyData) {
    override fun toString(): String {
      return "Format: $format Algorithm: $algorithm"
    }
  }
}


