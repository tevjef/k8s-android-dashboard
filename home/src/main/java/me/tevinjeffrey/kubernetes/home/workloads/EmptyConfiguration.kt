package me.tevinjeffrey.kubernetes.home.workloads

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.include_empty.title

class EmptyConfiguration(val view: ViewGroup) : LayoutContainer {
  override val containerView: View?
    get() = view

  var isVisible: Boolean
    get() = view.isVisible
    set(value) = { view.isVisible = value }()

  fun setTitle(emptyTitle: String) {
    title.text = emptyTitle
  }
}