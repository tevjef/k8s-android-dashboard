package me.tevinjeffrey.kubernetes.home.settings.adapter

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_settings.*
import me.tevinjeffrey.kubernetes.home.R

data class TokenItem constructor(
    private val title: String,
    val requestCode: Int) : Item(title.hashCode().toLong()) {

  var body: String = ""
  var isEnabled: Boolean = true

  override fun getLayout() = R.layout.item_settings

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.containerView.isEnabled = isEnabled
    viewHolder.title.isEnabled = isEnabled
    viewHolder.body.isEnabled = isEnabled

    if (body.isEmpty()) {
      viewHolder.body.text = viewHolder.containerView.context.getString(R.string.not_set)
    } else {
      viewHolder.body.text = body
    }
    viewHolder.title.text = title
  }
}