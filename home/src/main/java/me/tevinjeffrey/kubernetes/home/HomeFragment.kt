package me.tevinjeffrey.kubernetes.home

import android.os.Bundle
import android.support.design.widget.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import me.tevinjeffrey.kubernetes.base.extensions.observe
import me.tevinjeffrey.kubernetes.base.support.BaseFragment
import me.tevinjeffrey.kubernetes.home.HomeModel.*
import timber.log.Timber

class HomeFragment : BaseFragment() {

  private val viewModel by lazy<HomeViewModel> { viewModel() }

  override fun layoutId() = R.layout.fragment_home

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    observe(viewModel.model) {
      if (it != null) {
        onModelEvent(it)
      }
    }

    viewModel.loadHome()

    toolbar.inflateMenu(R.menu.menu_home)
    toolbar.setOnMenuItemClickListener { item ->
      false
    }

  }

  private fun onModelEvent(it: HomeModel) {
    super.onModelEvent(it)
    when (it) {
      is HomeSuccess -> onHomeSuccess(it)
      is HomeFailure -> onHomeFailure(it)
      is HomeProgress -> showLoading(it.isLoading)
    }
  }

  private fun onHomeFailure(model: HomeFailure) {
    Timber.e(model.error)
    Snackbar.make(view!!, model.error.message.toString(), Snackbar.LENGTH_LONG).show()
  }

  private fun showLoading(show: Boolean) {
    swipeRefresh.isRefreshing = show
  }

  private fun onHomeSuccess(it: HomeSuccess) {
    showLoading(false)

    val groupAdapter = GroupAdapter<ViewHolder>()
    groupAdapter.addAll(it.items.map { PodListItem(it) })
    list.adapter = groupAdapter
  }
}
