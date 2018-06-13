package me.tevinjeffrey.kubernetes.base.extensions

import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * RxJava utility class.
 */
class RxUtils private constructor() {
  init {
    throw AssertionError("No instances.")
  }

  companion object {

    val WINDOW_DURATION = 500


    /**
     * Dispose the disposables if they are not disposed
     */
    fun disposeIfNeeded(vararg disposables: Disposable) {
      for (disposable in disposables) {
        disposable.disposeIfNeeded()
      }
    }
  }
}

/**
 * Dispose if the disposable is not disposed
 */
fun Disposable?.disposeIfNeeded() {
  if (this.inFlight()) {
    this?.dispose()
  }
}

/**
 * Returns a boolean indicating whether a subscription is already being made
 */
fun Disposable?.inFlight(): Boolean {
  return this != null && !this.isDisposed
}

/**
 * Throttle using [::WINDOW_DURATION][RxUtils] milliseconds.
 */
inline fun <reified T> withThrottle(): ObservableTransformer<T, T> {
  return ObservableTransformer { observable -> observable.throttleFirst(RxUtils.WINDOW_DURATION.toLong(), TimeUnit.MILLISECONDS) }
}
