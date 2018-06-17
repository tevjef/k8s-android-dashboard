package me.tevinjeffrey.kubernetes.home.settings.adapter

import androidx.core.view.isGone
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_settings_toggle.*
import me.tevinjeffrey.kubernetes.home.R

class ProxyToggleItem constructor(
    private val title: String,
    private val checkedChanges: (Boolean) -> Unit
) : Item() {

  var body: String = ""
  var isChecked = true

  override fun getLayout() = R.layout.item_settings_toggle

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.toggle.isChecked = isChecked
    viewHolder.containerView.isEnabled = isChecked
    viewHolder.title.isEnabled = isChecked
    viewHolder.body.isEnabled = isChecked

    viewHolder.body.isGone = body.isEmpty()
    if (body.isEmpty()) {
      viewHolder.body.text = viewHolder.containerView.context.getString(R.string.not_set)
    } else {
      viewHolder.body.text = body
    }
    viewHolder.title.text = title

    viewHolder.toggle.setOnCheckedChangeListener { _, b ->
      isChecked = b
      checkedChanges(b)
    }
  }
}
