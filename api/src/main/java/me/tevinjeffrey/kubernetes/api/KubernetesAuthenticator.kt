package me.tevinjeffrey.kubernetes.api

import com.google.gson.Gson
import com.prolificinteractive.patrons.StringPreference
import okhttp3.Authenticator
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import java.io.IOException

class KubernetesAuthenticator(
    private val accessToken: StringPreference,
    private val refreshToken: StringPreference,
    private val url: HttpUrl,
    private val gson: Gson) : Authenticator {
  private val client: OkHttpClient

  init {
    val clientBuilder = OkHttpClient().newBuilder()
    if (BuildConfig.DEBUG) {
      val httpLoggingInterceptor = HttpLoggingInterceptor()
      httpLoggingInterceptor.level = BODY
      clientBuilder.addInterceptor(httpLoggingInterceptor)
    }
    this.client = clientBuilder.build()
  }

  @Throws(IOException::class)
  override fun authenticate(route: Route, response: Response): Request? {
    synchronized(client) {
      // Authorize requests
      return response
          .request()
          .newBuilder()
          .build()
    }
  }
}
