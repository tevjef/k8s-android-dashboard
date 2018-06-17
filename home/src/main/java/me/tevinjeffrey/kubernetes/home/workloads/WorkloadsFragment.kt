package me.tevinjeffrey.kubernetes.home.workloads

import android.os.Bundle
import androidx.recyclerview.widget.SimpleItemAnimator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_settings.*
import me.tevinjeffrey.kubernetes.base.support.BaseFragment
import me.tevinjeffrey.kubernetes.home.R

class WorkloadsFragment : BaseFragment() {

  private val adapter = GroupAdapter<ViewHolder>()

  override fun layoutId() = R.layout.fragment_workloads

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    list.adapter = adapter
    toolbar.inflateMenu(R.menu.search_filter)
  }
}
