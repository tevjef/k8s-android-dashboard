package me.tevinjeffrey.kubernetes.base.extensions

import android.arch.lifecycle.Lifecycle
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

fun View.disable() {
  isEnabled = false
}

fun View.enable() {
  isEnabled = true
}

fun View.visible() {
  visibility = View.VISIBLE
}

fun View.invisible() {
  visibility = View.INVISIBLE
}

fun View.gone() {
  visibility = View.GONE
}

fun View.slideExit() {
  if (translationY == 0f) animate().translationY(-height.toFloat())
}

fun View.slideEnter() {
  if (translationY < 0f) animate().translationY(0f)
}

fun View.fadeOut() {
  if (alpha > 0F) animate().alpha(0F)
}

fun View.fadeIn() {
  if (alpha == 0F) animate().alpha(1F)
}

fun View.clicksWithThrottle(block: () -> Unit) {
  this.clicks()
      .compose(withThrottle())
      .observeOn(AndroidSchedulers.mainThread())
      .`as`(AutoDispose.autoDisposable(
          AndroidLifecycleScopeProvider.from(this.findLifecycleOwner(), Lifecycle.Event.ON_DESTROY))
      )
      .subscribe({block.invoke()}, Timber::e)
}
