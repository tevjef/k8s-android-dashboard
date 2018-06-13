package me.tevinjeffrey.kubernetes.base.di.modules

import android.app.Application
import android.content.SharedPreferences
import me.tevinjeffrey.kubernetes.base.di.AccessToken
import me.tevinjeffrey.kubernetes.base.di.PerApp
import me.tevinjeffrey.kubernetes.base.di.RefreshToken
import com.prolificinteractive.patrons.StringPreference
import dagger.Module
import dagger.Provides

@Module
class PrefModule(val app: Application) {

  @Provides
  @PerApp
  fun provideSharedPrefs(): SharedPreferences = app.getSharedPreferences("Kubernetes", 0)

  @Provides
  @PerApp
  @AccessToken
  fun provideAccessToken(prefs: SharedPreferences) = StringPreference(prefs, "access_token")

  @Provides
  @PerApp
  @RefreshToken
  fun provideRefreshToken(prefs: SharedPreferences) = StringPreference(prefs, "refresh_token")
}
