package me.tevinjeffrey.kubernetes.base.di

import android.view.inputmethod.InputMethodManager
import me.tevinjeffrey.kubernetes.api.KubernetesApi
import me.tevinjeffrey.kubernetes.base.KubernetesApp
import me.tevinjeffrey.kubernetes.base.di.modules.ApiModule
import me.tevinjeffrey.kubernetes.base.di.modules.DataModule
import me.tevinjeffrey.kubernetes.base.di.modules.KubernetesAppModule
import me.tevinjeffrey.kubernetes.base.di.modules.PrefModule
import com.prolificinteractive.patrons.StringPreference
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import io.fabric8.kubernetes.client.KubernetesClient

@PerApp
@Component(modules = [
  KubernetesAppModule::class,
  DataModule::class,
  ApiModule::class,
  PrefModule::class,

  AndroidInjectionModule::class,
  AndroidSupportInjectionModule::class
])
interface KubernetesAppComponent {
  fun inject(app: KubernetesApp)

  fun inputMethodService(): InputMethodManager
  fun pandroidApi(): KubernetesApi
  fun kubernetesClient(): KubernetesClient

  @AccessToken fun accessToken(): StringPreference
  @RefreshToken fun refreshToken(): StringPreference

  @Component.Builder interface Builder {
    @BindsInstance fun application(app: KubernetesApp): Builder
    fun apiModule(module: ApiModule): Builder
    fun appModule(module: KubernetesAppModule): Builder
    fun dataModule(module: DataModule): Builder
    fun prefModule(module: PrefModule): Builder

    fun build(): KubernetesAppComponent
  }
}
