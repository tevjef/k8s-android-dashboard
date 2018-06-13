package me.tevinjeffrey.kubernetes.base

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import com.jakewharton.threetenabp.AndroidThreeTen
import me.tevinjeffrey.kubernetes.base.di.*
import me.tevinjeffrey.kubernetes.base.di.modules.ApiModule
import me.tevinjeffrey.kubernetes.base.di.modules.KubernetesAppModule
import me.tevinjeffrey.kubernetes.base.di.modules.PrefModule
import dagger.android.*
import javax.inject.Inject

class KubernetesApp : BaseKubernetesApp(), HasActivityInjector, HasBroadcastReceiverInjector, HasServiceInjector {

  @Inject lateinit var activityDispatchingModularInjector: ModularActivityInjector
  @Inject lateinit var broadcastReceiverDispatchingAndroidInjector: DispatchingAndroidInjector<BroadcastReceiver>
  @Inject lateinit var serviceDispatchingAndroidInjector: DispatchingAndroidInjector<Service>
  @Inject lateinit var appInjector: AppInjector

  lateinit var component: KubernetesAppComponent

  override fun onCreate() {
    super.onCreate()

    AndroidThreeTen.init(this)

    component = DaggerKubernetesAppComponent.builder()
        .apiModule(ApiModule(this))
        .appModule(KubernetesAppModule(this))
        .prefModule(PrefModule(this))
        .application(this)
        .build()
    component.inject(this)
    appInjector.init()
  }

  override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastReceiverDispatchingAndroidInjector

  override fun serviceInjector(): AndroidInjector<Service> = serviceDispatchingAndroidInjector

  override fun activityInjector(): AndroidInjector<Activity> = activityDispatchingModularInjector
}
