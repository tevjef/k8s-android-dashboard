package me.tevinjeffrey.kubernetes.base.drawer

import android.os.Bundle
import me.tevinjeffrey.kubernetes.base.support.BaseActivity
import me.tevinjeffrey.kubernetes.base.R
import me.tevinjeffrey.kubernetes.base.Router
import io.palaima.debugdrawer.commons.BuildModule
import io.palaima.debugdrawer.commons.DeviceModule
import io.palaima.debugdrawer.commons.NetworkModule
import io.palaima.debugdrawer.commons.SettingsModule
import io.palaima.debugdrawer.timber.TimberModule
import io.palaima.debugdrawer.view.DebugView
import javax.inject.Inject

class DebugActivity : BaseActivity() {

  @Inject lateinit var router: Router

  override fun layoutId() = R.layout.activity_debugdrawer

  override fun setupDependencyInjection() {
    DaggerActivityComponent.builder()
        .activity(this)
        .appComponent(appComponent)
        .build()
        .inject(this)
  }

  private lateinit var debugView: DebugView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    debugView = findViewById(R.id.debug_view)
    debugView.modules(
        TimberModule(),
        DeviceModule(),
        BuildModule(),
        NetworkModule(),
        SettingsModule()
    )
  }

  override fun onResume() {
    super.onResume()
    debugView.onResume()
  }

  override fun onPause() {
    super.onPause()
    debugView.onPause()
  }

  override fun onStart() {
    super.onStart()
    debugView.onStart()
  }

  override fun onStop() {
    super.onStop()
    debugView.onStop()
  }
}
