package me.tevinjeffrey.kubernetes.home.workloads.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView.BufferType.SPANNABLE
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_deployment.podCount
import kotlinx.android.synthetic.main.item_deployment.statusImage
import kotlinx.android.synthetic.main.item_deployment.title
import me.tevinjeffrey.kubernetes.home.R

data class WorkloadItem constructor(
    val id: String,
    val title: String,
    val running: Int,
    val desired: Int,
    val labels: List<String> = emptyList(),
    val ready: Boolean = true,
    val highlightSpanText: String = "") : Item(id.hashCode().toLong()) {

  override fun getLayout() = R.layout.item_deployment

  override fun bind(viewHolder: ViewHolder, position: Int) {
    val spanColor = ResourcesCompat.getColor(
        viewHolder.containerView.context.resources,
        R.color.colorPrimary,
        null
    )
    val spannedTitle = SpannableString(title)

    if (highlightSpanText.isNotEmpty()) {
      val start = title.indexOfAny(listOf(highlightSpanText))
      val end = start + highlightSpanText.length
      spannedTitle.setSpan(ForegroundColorSpan(spanColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    viewHolder.title.setText(spannedTitle, SPANNABLE)
    viewHolder.podCount.text = "$running/$desired"

    viewHolder.statusImage.setImageDrawable(
        AppCompatResources.getDrawable(viewHolder.containerView.context, if (ready) {
      R.drawable.ic_check_ok
    } else {
      R.drawable.ic_not_ready
    }))
  }
}