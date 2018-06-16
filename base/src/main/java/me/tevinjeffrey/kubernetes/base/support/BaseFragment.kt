package me.tevinjeffrey.kubernetes.base.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.tevinjeffrey.kubernetes.base.R
import me.tevinjeffrey.kubernetes.base.extensions.getViewModel
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment : ViewLifecycleFragment() {

  @Inject lateinit var factory: ViewModelProvider.Factory

  abstract fun layoutId(): Int

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?) = inflater.inflate(layoutId(), container, false)
      .apply { this.setTag(R.id.fragment_lifecycle_tag, viewLifecycleOwner) }

  inline fun <reified T : ViewModel> viewModel(activityScoped: Boolean = false): T  {
    return getViewModel(factory, activityScoped)
  }

  inline fun <reified T : ViewModel> withViewModel(activityScoped: Boolean = false, body: T.() -> Unit): T {
    val vm = getViewModel<T>(factory, activityScoped)
    vm.body()
    return vm
  }
}
