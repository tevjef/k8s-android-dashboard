package me.tevinjeffrey.kubernetes.base.di.modules

import android.app.Application
import android.content.SharedPreferences
import com.prolificinteractive.patrons.BooleanPreference
import com.prolificinteractive.patrons.StringPreference
import dagger.Module
import dagger.Provides
import me.tevinjeffrey.kubernetes.base.di.*

@Module
class PrefModule(val app: Application) {

  @Provides
  @PerApp
  fun provideSharedPrefs(): SharedPreferences = app.getSharedPreferences("kubernetes_connection", 0)
}
