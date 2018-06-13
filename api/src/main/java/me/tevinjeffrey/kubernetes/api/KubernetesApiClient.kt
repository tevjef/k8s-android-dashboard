package me.tevinjeffrey.kubernetes.api

import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class KubernetesApiClient(
    private val api: KubernetesApi,
    private val schedulers: RxSchedulers) : KubernetesApi, RxSchedulers {

  override fun <T> applySchedulers(): ObservableTransformer<T, T> = schedulers.applySchedulers()

  override fun data(): Observable<String> {
    return api.data().compose(applySchedulers())
  }
}
