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

  @Provides
  @PerApp
  @OAuthToken
  fun provideOAuthToken(prefs: SharedPreferences) = StringPreference(prefs, "oauth_token")

  @Provides
  @PerApp
  @ClientCert
  fun provideClientCertificate(prefs: SharedPreferences) = StringPreference(prefs, "client_cert")

  @Provides
  @PerApp
  @ClientKey
  fun provideClientKey(prefs: SharedPreferences) = StringPreference(prefs, "client_key")

  @Provides
  @PerApp
  @ClusterCACert
  fun provideClusterCA(prefs: SharedPreferences) = StringPreference(prefs, "cluster_ca_cert")

  @Provides
  @PerApp
  @AllowInsecure
  fun provideAllowInsecure(prefs: SharedPreferences) = BooleanPreference(prefs, "allow_insecure", false)

  @Provides
  @PerApp
  @MasterUrl
  fun provideMasterUrl(prefs: SharedPreferences) = StringPreference(prefs, "master_url")

  @Provides
  @PerApp
  @ShouldProxy
  fun provideShouldProxy(prefs: SharedPreferences) = BooleanPreference(prefs, "should_proxy", false)

  @Provides
  @PerApp
  @ProxyUrl
  fun provideProxyUrl(prefs: SharedPreferences) = StringPreference(prefs, "proxy_url")
}
