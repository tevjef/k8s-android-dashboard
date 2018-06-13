package me.tevinjeffrey.kubernetes.base

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

abstract class BaseKubernetesApp : Application() {

  override fun onCreate() {
    super.onCreate()

    Fabric.with(this, Crashlytics())
  }
}
