package me.tevinjeffrey.kubernetes.base.drawer

import android.app.Activity
import me.tevinjeffrey.kubernetes.base.di.KubernetesAppComponent
import me.tevinjeffrey.kubernetes.base.di.PerActivity
import dagger.BindsInstance
import dagger.Component

@PerActivity
@Component(dependencies = [KubernetesAppComponent::class])
interface ActivityComponent {

  fun inject(activity: DebugActivity)

  @Component.Builder
  interface Builder {
    fun appComponent(appComponent: KubernetesAppComponent): Builder
    fun build(): ActivityComponent

    @BindsInstance
    fun activity(activity: Activity): Builder
  }
}
