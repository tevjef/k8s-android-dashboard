package me.tevinjeffrey.kubernetes.home.dashboard

import android.os.Bundle
import androidx.recyclerview.widget.SimpleItemAnimator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_settings.*
import me.tevinjeffrey.kubernetes.base.support.BaseFragment
import me.tevinjeffrey.kubernetes.home.R

class WorkloadsFragment : BaseFragment() {

  private val adapter = GroupAdapter<ViewHolder>()

  override fun layoutId() = R.layout.fragment_settings

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    list.adapter = adapter
    (list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    toolbar.title = getString(R.string.settings)
  }
}
