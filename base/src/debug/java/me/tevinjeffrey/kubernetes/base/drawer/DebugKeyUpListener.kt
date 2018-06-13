package me.tevinjeffrey.kubernetes.base.drawer

import android.app.Activity
import android.content.Intent
import android.view.KeyEvent
import me.tevinjeffrey.kubernetes.base.KeyUpListener

class DebugKeyUpListener : KeyUpListener {

  private var index = 0
  private val SEQUENCE = intArrayOf(
      KeyEvent.KEYCODE_VOLUME_DOWN,
      KeyEvent.KEYCODE_VOLUME_DOWN,
      KeyEvent.KEYCODE_VOLUME_DOWN
  )

  override fun onKeyUp(activity: Activity, keyCode: Int, event: KeyEvent?) {
    if (isOnSecretActivity(activity)) {
      return
    }
    if (keyCode == SEQUENCE[index] && index == SEQUENCE.size - 1) {
      index = 0
      activity.startActivity(Intent(activity, DebugActivity::class.java))
    } else if (keyCode == SEQUENCE[index]) {
      index++
    } else {
      index = 0
    }
  }

  private fun isOnSecretActivity(activity: Activity): Boolean = activity.javaClass.simpleName == DebugActivity::class.java.simpleName
}
