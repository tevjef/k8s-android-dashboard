package me.tevinjeffrey.kubernetes.base.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.tevinjeffrey.kubernetes.api.RxSchedulers
import me.tevinjeffrey.kubernetes.api.typeadapters.ZonedDateTimeConverter
import me.tevinjeffrey.kubernetes.base.di.PerApp
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit

@Module class DataModule {
  @Provides
  @PerApp
  fun provideGson(): Gson = GsonBuilder()
      .setLenient()
      .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeConverter())
      .create()

  @Provides
  @PerApp
  fun provideOkHttpClient() = OkHttpClient.Builder()
      .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
      .readTimeout(TIMEOUT, TimeUnit.SECONDS)
      .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
      .build()!!

  @Provides
  @PerApp
  fun provideScheduler() = RxSchedulers.DEFAULT

  companion object {
    private const val TIMEOUT = 30L
  }
}
