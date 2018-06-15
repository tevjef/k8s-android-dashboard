package me.tevinjeffrey.kubernetes.base.extensions

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import me.tevinjeffrey.kubernetes.base.R
import java.lang.ref.WeakReference

object LifecycleExtensions {
  /**
   * Recurse up the view hierarchy, looking for the NavController
   * @param view the view to search from
   * @return the locally scoped [NavController] to the given view, if found
   */
  fun findViewLifecycle(view: View): LifecycleOwner? {
    var view: View? = view
    while (view != null) {
      val lifecycle = getViewLifecycle(view)

      if (lifecycle != null) {
        return lifecycle
      }
      val parent = view.parent
      view = if (parent is View) parent else null
    }
    return null
  }

  private fun getViewLifecycle(view: View): LifecycleOwner? {
    val tag = view.getTag(R.id.fragment_lifecycle_tag)
    var lifecycleOwner: LifecycleOwner? = null
    if (tag is WeakReference<*>) {
      lifecycleOwner = (tag as WeakReference<LifecycleOwner>).get()
    } else if (tag is LifecycleOwner) {
      lifecycleOwner = tag
    }
    return lifecycleOwner
  }
}

fun View.findLifecycleOwner(): LifecycleOwner? {
  return LifecycleExtensions.findViewLifecycle(this)
}
