package me.tevinjeffrey.kubernetes.api

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Logs errors HTTP responses with status codes >= 400
 */
class KubernetesApiInterceptor(private val gson: Gson) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Chain): Response {
    val response = chain.proceed(chain.request())
    if (response.code() >= 400) {
      Timber.e(ApiException(response.body()!!.charStream().toString()))
    }
    return response
  }
}
