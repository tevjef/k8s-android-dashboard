package me.tevinjeffrey.kubernetes.home.workloads.adapter

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_filter.chipGroup
import me.tevinjeffrey.kubernetes.base.widgets.ResString
import me.tevinjeffrey.kubernetes.entities.kubernetes.Namespace
import me.tevinjeffrey.kubernetes.home.R

data class NamespaceFilter constructor(
    private val chips: Set<ResString>,
    val selectedChips: MutableSet<ResString>,
    private val collapsed: Boolean = true,
    val mutatedChipsData: MutableLiveData<Set<ResString>> = MutableLiveData()) : Item(1) {

  private lateinit var defaultChip: Chip

  override fun getLayout() = R.layout.item_filter

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.containerView.setHasTransientState(true)
    val context = viewHolder.containerView.context
    val inflater = LayoutInflater.from(viewHolder.containerView.context)

    viewHolder.chipGroup.removeAllViews()
    chips.forEach { text ->
      val allChips = viewHolder.chipGroup.children.map { it as Chip }
      val chip = allChips.find { it.getTag(R.id.namespace) == text } ?:
      // If null inflate and add chip to chip group
      (inflater.inflate(R.layout.item_chip, viewHolder.chipGroup, false) as Chip)
          .apply {
            viewHolder.chipGroup.addView(this)
          }
      chip.setTag(R.id.namespace, text)

      val chipText = text.asString(context).toString()

      if (chipText == Namespace.Default.value) {
        defaultChip = chip
      }

      chip.chipText = if (collapsed) context.getString(R.string.namespaces_chip, chipText) else chipText
      chip.isChecked = selectedChips.any { it == text }
      chip.id = View.generateViewId()
      chip.isGone = !selectedChips.any { it == text } && collapsed
      chip.isEnabled = !collapsed
      chip.transitionName = chipText

      chip.setOnCheckedChangeListener { btn, isChecked ->
        val defaultString = ResString(apiString = Namespace.Default.value)
        val changedChip = btn as Chip

        val title = changedChip.getTag(R.id.namespace) as ResString

        if (isChecked) {
          selectedChips.add(title)
        } else {
          if ((changedChip == defaultChip && selectedChips.size > 1) || changedChip != defaultChip) {
            selectedChips.remove(title)
          }
        }

        if (selectedChips.isEmpty()) {
          selectedChips.add(defaultString)
        }

        if (selectedChips.size == 1 && selectedChips.contains(defaultString)) {
          defaultChip.isChecked = true
          defaultChip.isCheckable = false
        } else {
          defaultChip.isChecked = selectedChips.contains(defaultString)
          defaultChip.isCheckable = true
        }

        mutatedChipsData.value = selectedChips
      }

      viewHolder.chipGroup.isGone = collapsed
    }
  }
}