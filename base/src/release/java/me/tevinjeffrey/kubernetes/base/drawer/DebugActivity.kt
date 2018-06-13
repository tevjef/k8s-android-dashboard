package me.tevinjeffrey.kubernetes.base.drawer

import me.tevinjeffrey.kubernetes.base.support.BaseActivity

abstract class DebugActivity : BaseActivity() {
  override fun layoutId(): Int = -1
}
