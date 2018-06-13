package me.tevinjeffrey.kubernetes.base.support

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Lifecycle.Event
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

/**
 * Fragment providing separate lifecycle owners for each created view hierarchy.
 *
 *
 * This is one possible way to solve issue https://github.com/googlesamples/android-architecture-components/issues/47
 * Remove when view lifecycles are exposed in support v28
 * https://youtu.be/pErTyQpA390?t=5m41s
 *
 * @author Christophe Beyls
 */
open class ViewLifecycleFragment : Fragment() {

  internal var viewLifecycleOwner: ViewLifecycleOwner? = null

  internal class ViewLifecycleOwner : LifecycleOwner {
    override fun getLifecycle(): Lifecycle = lifecycle

    val lifecycle = LifecycleRegistry(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewLifecycleOwner = ViewLifecycleOwner()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Event.ON_CREATE)
  }

  override fun onStart() {
    super.onStart()
    viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Event.ON_START)
  }

  override fun onResume() {
    super.onResume()
    viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Event.ON_RESUME)
  }

  override fun onPause() {
    viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Event.ON_PAUSE)
    super.onPause()
  }

  override fun onStop() {
    viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Event.ON_STOP)
    super.onStop()
  }

  override fun onDestroyView() {
    viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Event.ON_DESTROY)
    viewLifecycleOwner = null
    super.onDestroyView()
  }
}
