package me.tevinjeffrey.kubernetes.base.di

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import me.tevinjeffrey.kubernetes.base.KubernetesApp
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

@PerApp
class AppInjector @Inject constructor(val app: KubernetesApp) {

  fun init() {
    app.registerActivityLifecycleCallbacks(object : EmptyActivityLifecycleCallbacks() {
      override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        handleActivity(activity)
      }
    })
  }

  private fun handleActivity(activity: Activity?) {
    if (activity is FragmentActivity) {
      try {
        AndroidInjection.inject(activity)
      } catch (e: IllegalArgumentException) {
        Timber.w("Unable to inject activity: ${activity.javaClass.simpleName}", e)
      }

      activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
          object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentCreated(fm: FragmentManager,
                                           fragment: Fragment,
                                           savedInstanceState: Bundle?) {
              handleFragment(fragment)
            }
          }, true)
    }
  }

  private fun handleFragment(fragment: Fragment?) {
    try {
      AndroidSupportInjection.inject(fragment)
    } catch (e: IllegalArgumentException) {
      Timber.w("Unable to inject fragment: ${fragment?.javaClass?.simpleName}", e)
    }
  }
}
