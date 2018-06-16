package me.tevinjeffrey.kubernetes.home.settings.adapter

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_settings.*
import me.tevinjeffrey.kubernetes.home.R

data class CertItem constructor(
    private val title: String,
    val requestCode: Int) : Item(requestCode.toLong()) {

  var location: String = ""
  var isEnabled: Boolean = true

  override fun getLayout() = R.layout.item_settings

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.containerView.isEnabled = isEnabled
    viewHolder.title.isEnabled = isEnabled
    viewHolder.body.isEnabled = isEnabled

    viewHolder.title.text = title
    viewHolder.body.text = location
  }
}