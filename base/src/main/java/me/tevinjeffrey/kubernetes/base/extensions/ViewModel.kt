package me.tevinjeffrey.kubernetes.base.extensions

import android.arch.lifecycle.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import me.tevinjeffrey.kubernetes.base.support.ViewLifecycleFragment

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(viewModelFactory: ViewModelProvider.Factory): T {
  return ViewModelProviders.of(this, viewModelFactory)[T::class.java]
}

inline fun <reified T : ViewModel> FragmentActivity.withViewModel(viewModelFactory: ViewModelProvider.Factory, body: T.() -> Unit): T {
  val vm = getViewModel<T>(viewModelFactory)
  vm.body()
  return vm
}

inline fun <reified T : ViewModel> Fragment.getViewModel(viewModelFactory: ViewModelProvider.Factory, activityScoped: Boolean = false): T {
  return if (activityScoped) {
    this.requireActivity().getViewModel(viewModelFactory)
  } else {
    ViewModelProviders.of(this, viewModelFactory)[T::class.java]
  }
}

inline fun <reified T : ViewModel> Fragment.withViewModel(viewModelFactory: ViewModelProvider.Factory, body: T.() -> Unit): T {
  val vm = getViewModel<T>(viewModelFactory)
  vm.body()
  return vm
}

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
  liveData.observe(this, Observer(body))
}

fun <T : Any, L : LiveData<T>> ViewLifecycleFragment.observe(liveData: L, body: (T?) -> Unit) {
  liveData.observe(viewLifecycleOwner!!, Observer(body))
}
