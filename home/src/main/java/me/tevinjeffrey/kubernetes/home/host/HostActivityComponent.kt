package me.tevinjeffrey.kubernetes.home.host

import android.app.Activity
import me.tevinjeffrey.kubernetes.base.KubernetesApp
import me.tevinjeffrey.kubernetes.base.di.KubernetesAppComponent
import me.tevinjeffrey.kubernetes.base.di.PerActivity
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector

@PerActivity
@Component(
    modules = [
      FragmentBindingModule::class,
      ViewModelBindingModule::class
    ],
    dependencies = [
      KubernetesAppComponent::class
    ]
)
interface HostActivityComponent : AndroidInjector<HostActivity> {

  @Component.Builder
  abstract class Builder : AndroidInjector.Builder<HostActivity>() {
    @BindsInstance
    abstract fun activity(activity: Activity): Builder
    abstract fun appComponent(appComponent: KubernetesAppComponent): Builder
    abstract override fun build(): HostActivityComponent
  }

  companion object {
    fun setupDependencyInjection(activity: Activity) {
      val modularActivityInjector = (activity.application as KubernetesApp).activityDispatchingModularInjector
      modularActivityInjector.register(HostActivity::class.java) { applicationComponent ->
        DaggerHostActivityComponent
            .builder()
            .activity(activity)
            .appComponent(applicationComponent)
      }
    }
  }
}
