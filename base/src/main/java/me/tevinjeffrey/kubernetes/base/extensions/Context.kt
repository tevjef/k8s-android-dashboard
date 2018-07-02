package me.tevinjeffrey.kubernetes.base.extensions

import android.content.Context
import android.os.Build
import android.util.TypedValue

fun Context.getThemePrimaryColor(): Int {
  val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    android.R.attr.colorPrimary
  } else {
    //Get colorAccent defined for AppCompat
    this.resources.getIdentifier("colorPrimary", "attr", this.packageName)
  }
  val outValue = TypedValue()
  this.theme.resolveAttribute(colorAttr, outValue, true)
  return outValue.data
}

fun Context.getThemeAccentColor(): Int {
  val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    android.R.attr.colorAccent
  } else {
    //Get colorAccent defined for AppCompat
    this.resources.getIdentifier("colorAccent", "attr", this.packageName)
  }
  val outValue = TypedValue()
  this.theme.resolveAttribute(colorAttr, outValue, true)
  return outValue.data
}