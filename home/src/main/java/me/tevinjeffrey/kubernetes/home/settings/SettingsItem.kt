package me.tevinjeffrey.kubernetes.home.settings

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_settings.*
import me.tevinjeffrey.kubernetes.home.R

data class SettingsItem constructor(
    private val title: String,
    val requestCode: Int) : Item(requestCode.toLong()) {

  var location: String = ""

  override fun getLayout() = R.layout.item_settings

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.title.text = title
    viewHolder.body.text = location
  }
}