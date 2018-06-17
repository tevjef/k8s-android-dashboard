package me.tevinjeffrey.kubernetes.base.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import me.tevinjeffrey.kubernetes.api.KubernetesClientProvider
import me.tevinjeffrey.kubernetes.api.ssl.HttpClientUtils
import me.tevinjeffrey.kubernetes.base.BuildConfig
import me.tevinjeffrey.kubernetes.base.di.PerApp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
class ApiModule(private val app: Application) {

  @Provides
  @PerApp
  fun providesKubernetesClientProvider
      (okHttpClient: OkHttpClient,
       httpClientUtils: HttpClientUtils): KubernetesClientProvider {
    return KubernetesClientProvider(okHttpClient, httpClientUtils)
  }

  @Provides
  @PerApp
  fun provideOkHttpClient(): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()

    if (BuildConfig.DEBUG) {
      val httpLoggingInterceptor = HttpLoggingInterceptor()
      httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
      clientBuilder.addInterceptor(httpLoggingInterceptor)
    }
    return clientBuilder.build()
  }
}
