package me.tevinjeffrey.kubernetes.api.extensions

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.io.Reader

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json,
    object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJson(reader: Reader) = this.fromJson<T>(reader,
    object : TypeToken<T>() {}.type)

fun JsonElement.toStringOrNull(): String? {
  if (!this.isJsonNull) {
    return this.asString
  }

  return null
}
