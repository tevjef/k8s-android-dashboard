package me.tevinjeffrey.kubernetes.api

import com.prolificinteractive.patrons.StringPreference
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class KubernetesAuthInterceptor(private val accessToken: StringPreference) : Interceptor {
  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    // Modify outgoing requests
    return chain.proceed(request)
  }
}
