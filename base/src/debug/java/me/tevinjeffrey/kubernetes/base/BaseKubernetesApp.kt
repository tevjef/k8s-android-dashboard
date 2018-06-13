package me.tevinjeffrey.kubernetes.base

import android.support.multidex.MultiDexApplication
import com.squareup.leakcanary.LeakCanary
import io.palaima.debugdrawer.timber.data.LumberYard
import timber.log.Timber

abstract class BaseKubernetesApp : MultiDexApplication() {

  override fun onCreate() {
    super.onCreate()

    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }

//    SoLoader.init(this, 0)
//
//    if (BuildConfig.DEBUG && SonarUtils.shouldEnableSonar(this)) {
//      val client = AndroidSonarClient.getInstance(this)
//
//      val descriptorMapping = DescriptorMapping.withDefaults()
//      client.addPlugin(InspectorSonarPlugin(this, descriptorMapping))
//      client.addPlugin(NetworkSonarPlugin())
//      client.start()
//    }

    LeakCanary.install(this)

    val lumberYard = LumberYard.getInstance(this)
    lumberYard.cleanUp()

    Timber.plant(lumberYard.tree())
    Timber.plant(Timber.DebugTree())
  }
}
