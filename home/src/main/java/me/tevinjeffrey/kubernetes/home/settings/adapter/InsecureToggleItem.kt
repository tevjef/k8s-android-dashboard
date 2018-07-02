package me.tevinjeffrey.kubernetes.home.settings.adapter

import androidx.core.view.isGone
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_settings_toggle.*
import me.tevinjeffrey.kubernetes.home.R

class InsecureToggleItem constructor(
    private val title: String,
    private val checkedChanges: (Boolean) -> Unit
) : Item() {

  var body: String = ""
  var isChecked  = false

  override fun getLayout() = R.layout.item_settings_toggle

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.toggle.isChecked = isChecked
    viewHolder.body.isGone = body.isEmpty()
    viewHolder.body.text = body
    viewHolder.title.text = title
    viewHolder.toggle.setOnCheckedChangeListener { _, b ->
      isChecked = b
      checkedChanges(b)
    }
  }
}
