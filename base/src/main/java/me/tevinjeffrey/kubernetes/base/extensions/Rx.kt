package me.tevinjeffrey.kubernetes.base.extensions

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
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

class Observables private constructor() {
  companion object {
    inline fun <T1, T2> combineLatest(o1: Observable<T1>, o2: Observable<T2>): Observable<Pair<T1, T2>> =
        Observable.combineLatest(o1, o2, BiFunction { t1, t2 -> t1 to t2 })
  }
}

inline fun <T1, T2, R> Observable<T1>.withLatestFrom(observable: Observable<T2>, crossinline combiner: (T1, T2) -> R): Observable<R> =
    withLatestFrom(observable, BiFunction { t1, t2 -> combiner.invoke(t1, t2) })

// Yeah, if you've read my blog, I was sceptical about operators, but if you'll use them carefully they'll help keep code readable.
inline operator fun CompositeDisposable.plusAssign(disposable: Disposable) = this.add(disposable).let { Unit }