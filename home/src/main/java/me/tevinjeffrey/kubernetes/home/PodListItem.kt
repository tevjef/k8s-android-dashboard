package me.tevinjeffrey.kubernetes.home

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_podlist.*

class PodListItem constructor(private val podName: String) : Item() {

  override fun getLayout() = R.layout.item_podlist

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.podName.text = podName
  }
}