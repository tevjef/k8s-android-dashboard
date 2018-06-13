package me.tevinjeffrey.kubernetes.api

import io.reactivex.Observable

interface KubernetesApi {
  fun data(): Observable<String>
}
