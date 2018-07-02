package me.tevinjeffrey.kubernetes.base.support


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import me.tevinjeffrey.kubernetes.base.R
import me.tevinjeffrey.kubernetes.base.extensions.getViewModel
import me.tevinjeffrey.kubernetes.base.support.BaseFragment.SnackbarType.NEGATIVE
import me.tevinjeffrey.kubernetes.base.support.BaseFragment.SnackbarType.NEUTRAL
import me.tevinjeffrey.kubernetes.base.support.BaseFragment.SnackbarType.POSITIVE
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

  @Inject
  lateinit var factory: ViewModelProvider.Factory

  abstract fun layoutId(): Int

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?) = inflater.inflate(layoutId(), container, false)
      .apply { this.setTag(R.id.fragment_lifecycle_tag, viewLifecycleOwner) }

  fun showSnackbar(text: String, type: SnackbarType = NEUTRAL) {
    val snackbar = Snackbar.make(view!!, text, Snackbar.LENGTH_LONG)
    val color = when (type) {
      POSITIVE -> me.tevinjeffrey.kubernetes.base.R.color.green_700
      NEGATIVE -> me.tevinjeffrey.kubernetes.base.R.color.red_700
      NEUTRAL -> me.tevinjeffrey.kubernetes.base.R.color.grey_900
    }
    snackbar.view.setBackgroundColor(ResourcesCompat.getColor(resources, color, resources.newTheme()))
    snackbar.show()
  }

  inline fun <reified T : ViewModel> viewModel(activityScoped: Boolean = false): T {
    return getViewModel(factory, activityScoped)
  }

  inline fun <reified T : ViewModel> withViewModel(activityScoped: Boolean = false, body: T.() -> Unit): T {
    val vm = getViewModel<T>(factory, activityScoped)
    vm.body()
    return vm
  }

  enum class SnackbarType {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
  }

  companion object {
    const val CLIENT_CERT_REQUEST_CODE = 42
    const val CLIENT_KEY_REQUEST_CODE = 43
    const val CLUSTER_CA_REQUEST_CODE = 44
    const val BEARER_TOKEN_REQUEST_CODE = 45
  }
}
