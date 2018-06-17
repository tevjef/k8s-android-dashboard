package me.tevinjeffrey.kubernetes.base.extensions

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
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

fun Any.toObservable() = Observable.just(this)
fun Any.toFlowable() = Flowable.just(this)
fun Any.toMaybe() = Maybe.just(this)
fun Any.toSingle() = Single.just(this)

inline fun <reified T> single(crossinline block: () -> T): Single<T> {
  return Single.create<T> {
    try {
      it.onSuccess(block())
    } catch (e: Exception) {
      it.onError(e)
    }
  }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
}

inline fun <reified T> maybe(crossinline block: () -> T): Maybe<T> {
  return Maybe.create<T> {
    try {
      it.onSuccess(block())
    } catch (e: Exception) {
      it.onError(e)
    }
    it.onComplete()
  }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
}

inline fun <reified T> observable(crossinline block: () -> T): Observable<T> {
  return Observable.create<T> {
    try {
      it.onNext(block())
    } catch (e: Exception) {
      it.onError(e)
    }
    it.onComplete()
  }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
}


inline fun <reified T> flowable(crossinline block: () -> T): Flowable<T> {
  return Flowable.create<T>({
    try {
      it.onNext(block())
    } catch (e: Exception) {
      it.onError(e)
    }
    it.onComplete()
  }, BackpressureStrategy.BUFFER)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
}