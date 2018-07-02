package me.tevinjeffrey.kubernetes.base.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*

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

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, removeObservers: Boolean = false, body: (T?) -> Unit) {
  if (removeObservers) {
    liveData.removeObservers(this)
  }
  liveData.observe(this, Observer(body))
}

fun <T : Any, L : LiveData<T>> Fragment.observe(liveData: L, removeObservers: Boolean = false, body: (T?) -> Unit) {
  if (removeObservers) {
    liveData.removeObservers(viewLifecycleOwner)
  }
  liveData.observe(viewLifecycleOwner, Observer(body))
}

fun <T> LiveData<T>.getDistinct(): LiveData<T> {
  val distinctLiveData = MediatorLiveData<T>()
  distinctLiveData.addSource(this, object : Observer<T> {
    private var initialized = false
    private var lastObj: T? = null
    override fun onChanged(obj: T?) {
      if (!initialized) {
        initialized = true
        lastObj = obj
        distinctLiveData.postValue(lastObj)
      } else if ((obj == null && lastObj != null)
          || obj != lastObj) {
        lastObj = obj
        distinctLiveData.postValue(lastObj)
      }
    }
  })
  return distinctLiveData
}