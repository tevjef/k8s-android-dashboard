package me.tevinjeffrey.kubernetes.db

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

inline fun <T> ConfigDatabase.observeClusterValue(liveData: MutableLiveData<T>, crossinline keySelector: (Cluster) -> T?) {
  this.configDao().getDistinctCluster()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeBy(
          onNext = { liveData.value = keySelector(it) },
          onError = { Timber.e(it) }
      )
}


inline fun <T> ConfigDatabase.observeClusterValue(crossinline callback: (T?) -> Unit, crossinline keySelector: (Cluster) -> T?) {
  this.configDao().getDistinctCluster()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeBy(
          onNext = { callback(keySelector(it)) },
          onError = { Timber.e(it) }
      )
}