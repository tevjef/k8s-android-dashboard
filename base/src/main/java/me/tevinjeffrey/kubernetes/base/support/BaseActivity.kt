package me.tevinjeffrey.kubernetes.base.support

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import me.tevinjeffrey.kubernetes.base.KeyUpListener
import me.tevinjeffrey.kubernetes.base.KubernetesApp
import me.tevinjeffrey.kubernetes.base.drawer.DebugKeyUpListener

abstract class BaseActivity : AppCompatActivity() {

  protected val appComponent get() = (applicationContext as KubernetesApp).component

  private val keyUpListener: KeyUpListener = DebugKeyUpListener()

  abstract fun layoutId(): Int

  abstract fun setupDependencyInjection()

  override fun onCreate(savedInstanceState: Bundle?) {
    setupDependencyInjection()
    super.onCreate(savedInstanceState)
    setContentView(layoutId())
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    keyUpListener.onKeyUp(this, keyCode, event)
    return super.onKeyUp(keyCode, event)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        finish()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }
}
