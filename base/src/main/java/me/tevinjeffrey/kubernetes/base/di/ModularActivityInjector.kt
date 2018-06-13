package me.tevinjeffrey.kubernetes.base.di

import android.app.Activity
import me.tevinjeffrey.kubernetes.base.KubernetesApp
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

@PerApp
class ModularActivityInjector @Inject constructor(
    private val decoratedAndroidInjector: DispatchingAndroidInjector<Activity>
) : AndroidInjector<Activity> by decoratedAndroidInjector {

  private val map = mutableMapOf<Class<out Activity>, (KubernetesAppComponent) -> AndroidInjector.Builder<out Activity>>()

  override fun inject(instance: Activity) {
    if (map.contains(instance.activityClass())) {
      inject(instance, instance.activityClass()!!)
    } else {
      decoratedAndroidInjector.inject(instance)
    }
  }

  fun <T : Activity> register(clazz: Class<T>, block: (KubernetesAppComponent) -> AndroidInjector.Builder<T>) {
    map[clazz] = block
  }

  private inline fun <reified T : Activity> inject(instance: Activity, clazz: Class<out T>) {
    val applicationComponent = (instance.applicationContext as KubernetesApp).component
    @Suppress("UNCHECKED_CAST")
    val builder = map[clazz]?.invoke(applicationComponent) as AndroidInjector.Builder<Activity>
    builder.create(instance.cast())?.inject(instance.cast())
  }

  private fun Activity?.activityClass(): Class<out Activity>? = this?.javaClass

  private inline fun <reified T : Activity> Activity?.cast(): T? = T::class.java.cast(this)
}
