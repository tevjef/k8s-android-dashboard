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
import me.tevinjeffrey.kubernetes.home.R

data class WorkloadTypeFilter constructor(
    private val chips: Set<ResString>,
    val selectedChips: MutableSet<ResString>,
    private val collapsed: Boolean = true,
    val mutatedChipsData: MutableLiveData<Set<ResString>> = MutableLiveData()) : Item(0) {

  override fun getLayout() = R.layout.item_filter

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.containerView.setHasTransientState(true)
    val context = viewHolder.containerView.context
    val inflater = LayoutInflater.from(viewHolder.containerView.context)

    viewHolder.chipGroup.removeAllViews()
    chips.forEach { workloadType ->
      val allChips = viewHolder.chipGroup.children.map { it as Chip }
      val chip = allChips.find {
       it.getTag(R.id.workload_type) == workloadType
      } ?:
      // If null inflate and add chip to chip group
      (inflater.inflate(R.layout.item_chip, viewHolder.chipGroup, false) as Chip)
          .apply {
            viewHolder.chipGroup.addView(this)
          }

      val chipText = workloadType.asString(context).toString()

      chip.chipText = if (collapsed) context.getString(R.string.workload_chip, chipText) else chipText
      chip.setTag(R.id.workload_type, workloadType)
      chip.isChecked = selectedChips.any { it == workloadType }
      chip.id = View.generateViewId()
      chip.isGone = !selectedChips.any { it == workloadType } && collapsed
      chip.isEnabled = !collapsed
      chip.transitionName = chipText
      chip.setOnCheckedChangeListener { _, isChecked ->
        val resString = chip.getTag(R.id.workload_type) as ResString
        if (isChecked) {
          selectedChips.add(resString)
        } else {
          selectedChips.remove(resString)
        }
        mutatedChipsData.value = selectedChips
      }

      viewHolder.chipGroup.isGone = collapsed
    }
  }
}