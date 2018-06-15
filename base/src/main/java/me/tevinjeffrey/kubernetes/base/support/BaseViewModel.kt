package me.tevinjeffrey.kubernetes.base.support

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
  val disposable: CompositeDisposable = CompositeDisposable()

  override fun onCleared() {
    super.onCleared()
    disposable.clear()
  }
}