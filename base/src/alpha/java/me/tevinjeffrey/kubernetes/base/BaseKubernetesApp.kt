package me.tevinjeffrey.kubernetes.base

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent
import io.fabric.sdk.android.Fabric
import io.palaima.debugdrawer.timber.data.LumberYard
import timber.log.Timber

abstract class BaseKubernetesApp : MultiDexApplication() {

  override fun onCreate() {
    super.onCreate()

    Fabric.with(this, Crashlytics())

    val lumberYard = LumberYard.getInstance(this)
    lumberYard.cleanUp()

    Timber.plant(lumberYard.tree())
    Timber.plant(Timber.DebugTree())
  }
}
