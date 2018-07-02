package me.tevinjeffrey.kubernetes.home.workloads.adapter

import android.view.LayoutInflater
import androidx.core.view.children
import androidx.core.view.isGone
import com.google.android.material.chip.Chip
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_filter.chipGroup
import me.tevinjeffrey.kubernetes.base.widgets.ResString
import me.tevinjeffrey.kubernetes.home.R

data class CombinedFilter constructor(
    private val namespaceChips: MutableSet<ResString> = mutableSetOf(),
    private val workloadChips: MutableSet<ResString> = mutableSetOf(),
    var collapsed: Boolean = false
) : Item(4) {

  override fun getLayout() = R.layout.item_filter

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.containerView.setHasTransientState(true)
    val context = viewHolder.containerView.context
    val inflater = LayoutInflater.from(viewHolder.containerView.context)

    val allChipsText = namespaceChips
        .map { context.getString(R.string.namespaces_chip, it.asString(context)) }
        .toMutableSet()

    allChipsText.addAll(workloadChips
        .map { context.getString(R.string.workload_chip, it.asString(context)) }
    )

    viewHolder.chipGroup.removeAllViews()
    allChipsText.forEach { text ->
      val allChips = viewHolder.chipGroup.children.map { it as Chip }
      val chip = allChips.find {
        val tag = it.getTag(R.id.workload_type)
        if (tag != null) it.getTag(R.id.workload_type) as String == text else false
      } ?:
      // If null inflate and add chip to chip group
      (inflater.inflate(R.layout.item_chip, viewHolder.chipGroup, false) as Chip)
          .apply {
            viewHolder.chipGroup.addView(this)
          }

      chip.chipText = text
      chip.setTag(R.id.workload_type, text)
      chip.isChecked = true
      chip.isEnabled = false
      chip.transitionName = text
    }
    viewHolder.chipGroup.isGone = collapsed
  }

  fun updateNamespaceChips(namespaces: Set<ResString>) {
    namespaceChips.clear()
    namespaceChips.addAll(namespaces)
  }

  fun updateWorkloadTypeChips(workloadTypes: Set<ResString>) {
    workloadChips.clear()
    workloadChips.addAll(workloadTypes)
  }
}