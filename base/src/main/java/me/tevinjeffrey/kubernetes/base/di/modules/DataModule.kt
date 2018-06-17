package me.tevinjeffrey.kubernetes.base.di.modules

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import me.tevinjeffrey.kubernetes.api.RxSchedulers
import me.tevinjeffrey.kubernetes.api.typeadapters.ZonedDateTimeConverter
import me.tevinjeffrey.kubernetes.base.di.PerApp
import me.tevinjeffrey.kubernetes.db.ConfigDatabase
import org.threeten.bp.ZonedDateTime

@Module
class DataModule(val app: Application) {

  @Provides
  @PerApp
  fun provideGson(): Gson = GsonBuilder()
      .setLenient()
      .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeConverter())
      .create()

  @Provides
  @PerApp
  fun provideConfigDatabase(): ConfigDatabase = ConfigDatabase.getInstance(app.applicationContext)

  @Provides
  @PerApp
  fun provideScheduler() = RxSchedulers.DEFAULT

  companion object {
    private const val TIMEOUT = 30L
  }
}
