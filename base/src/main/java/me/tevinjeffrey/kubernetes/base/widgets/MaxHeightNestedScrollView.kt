package me.tevinjeffrey.kubernetes.base.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import me.tevinjeffrey.kubernetes.base.R
import timber.log.Timber

class MaxHeightNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

  private var maxHeight = WITHOUT_MAX_HEIGHT_VALUE

  init {
    val a = context.theme.obtainStyledAttributes(
        attrs,
        R.styleable.MaxHeightNestedScrollView,
        0, 0)

    try {
      setMaxHeight(a.getDimension(R.styleable.MaxHeightNestedScrollView_maxHeight, 0f))
    } finally {
      a.recycle()
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    var heightMeasureSpec = heightMeasureSpec
    if (maxHeight > 0) {
      val hSize = View.MeasureSpec.getSize(heightMeasureSpec)
      val hMode = View.MeasureSpec.getMode(heightMeasureSpec)

      when (hMode) {
        View.MeasureSpec.AT_MOST -> heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.min(hSize, maxHeight.toInt()), View.MeasureSpec.AT_MOST)
        View.MeasureSpec.UNSPECIFIED -> heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxHeight.toInt(), View.MeasureSpec.AT_MOST)
        View.MeasureSpec.EXACTLY -> heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.min(hSize, maxHeight.toInt()), View.MeasureSpec.EXACTLY)
      }
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

  }

  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
    Timber.d(ev.toString())
    return super.onInterceptTouchEvent(ev)
  }

  fun setMaxHeight(maxHeight: Float) {
    this.maxHeight = maxHeight
  }

  companion object {

    var WITHOUT_MAX_HEIGHT_VALUE = -1f
  }
}