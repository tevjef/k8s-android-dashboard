package me.tevinjeffrey.kubernetes.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

class ApiUtil {
  companion object {
    inline fun <reified T> Gson.fromJson(json: String)
        = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    inline fun <reified T> Gson.fromJson(reader: Reader)
        = this.fromJson<T>(reader, object : TypeToken<T>() {}.type)
  }
}

