package me.tevinjeffrey.kubernetes.setup.cert

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_podlist.*
import me.tevinjeffrey.kubernetes.home.R

class PodListItem constructor(private val podName: String) : Item() {

  override fun getLayout() = R.layout.item_podlist

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.podName.text = podName
  }
}