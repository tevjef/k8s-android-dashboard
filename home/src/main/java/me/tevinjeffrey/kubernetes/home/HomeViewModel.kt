package me.tevinjeffrey.kubernetes.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.fabric8.kubernetes.client.KubernetesClient
import me.tevinjeffrey.kubernetes.api.KubernetesApi
import me.tevinjeffrey.kubernetes.home.HomeEvent.LoadHomeData
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val client: KubernetesClient) : ViewModel() {

  internal val uiEvents: Subject<HomeEvent> = PublishSubject.create()
  internal val model: LiveData<HomeModel>
    get() {
      return data
    }

  private val data: MutableLiveData<HomeModel> = MutableLiveData()

  init {
    Timber.d("HomeViewModel#${hashCode()} ")
    uiEvents
        .doOnNext { Timber.d("--> event: ${it.javaClass.simpleName} -- $it") }
        .publish { shared ->
          Observable.merge(listOf(
              shared
                  .ofType(LoadHomeData::class.java)
                  .compose(home())
          ))
        }
        .subscribe(data::setValue, Timber::e)
  }

  fun loadHome() {
    uiEvents.onNext(LoadHomeData)
  }

  fun home(): ObservableTransformer<in LoadHomeData, out HomeModel> {
    return ObservableTransformer {
      it.flatMap { event ->
        Observable.create<List<String>> {
          it.onNext(client.pods().inNamespace("staging").list().items.map { it.metadata.name })
          it.onComplete()
        }
            .map { HomeModel.HomeSuccess(it) }
            .cast(HomeModel::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { HomeModel.HomeFailure(it) }
            .startWith(HomeModel.HomeProgress(true))
      }
    }
  }
}
