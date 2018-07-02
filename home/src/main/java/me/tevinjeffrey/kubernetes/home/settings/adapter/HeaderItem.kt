package me.tevinjeffrey.kubernetes.home.settings.adapter

import androidx.core.view.isGone
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_header.*
import me.tevinjeffrey.kubernetes.home.R

class HeaderItem constructor(private val title: String) : Item(title.hashCode().toLong()) {

  override fun getLayout() = R.layout.item_header

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.divider.isGone = true
    viewHolder.header.text = title
  }
}