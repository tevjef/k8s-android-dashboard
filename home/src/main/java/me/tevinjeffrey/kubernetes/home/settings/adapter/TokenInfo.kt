package me.tevinjeffrey.kubernetes.home.settings.adapter

class TokenInfo(
    val data: String,
    val iss: String,
    val sub: String
) {
  override fun toString(): String {
    return sub
  }
}