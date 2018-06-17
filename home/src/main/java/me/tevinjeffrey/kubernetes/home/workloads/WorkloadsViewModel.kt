package me.tevinjeffrey.kubernetes.home.workloads

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import me.tevinjeffrey.kubernetes.db.Cluster
import me.tevinjeffrey.kubernetes.db.ConfigDatabase
import me.tevinjeffrey.kubernetes.home.settings.adapter.SpinnerDisplay
import javax.inject.Inject

class WorkloadsViewModel @Inject constructor(configDatabase: ConfigDatabase) : BaseViewModel() {

  val spinnerLiveData: MutableLiveData<SpinnerDisplay> = MutableLiveData()
  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()
  val message: SingleLiveEvent<String> = SingleLiveEvent()
  private val configDao = configDatabase.configDao()

  init {
    Observable.combineLatest(
        configDao
            .watchClusters()
            .toObservable(),
        configDao
            .watchCurrentCluster()
            .toObservable(),
        BiFunction { t1: List<Cluster>, t2: Cluster -> SpinnerDisplay(t2, t1) }
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onNext = { spinnerLiveData.value = it },
            onError = { error.value = it }
        )
  }
}

