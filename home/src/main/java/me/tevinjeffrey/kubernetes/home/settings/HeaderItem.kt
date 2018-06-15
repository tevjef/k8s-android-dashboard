package me.tevinjeffrey.kubernetes.home.settings

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_header.*
import me.tevinjeffrey.kubernetes.home.R

class HeaderItem constructor(private val title: String) : Item() {

  override fun getLayout() = R.layout.item_header

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.header.text = title
  }
}