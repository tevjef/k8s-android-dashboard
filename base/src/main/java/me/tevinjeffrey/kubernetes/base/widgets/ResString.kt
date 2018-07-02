package me.tevinjeffrey.kubernetes.base.widgets

import android.content.Context

data class ResString(
    val resString: String = "",
    val apiString: String = "") {
  fun asString(context: Context): CharSequence {
    if (resString.isEmpty() && apiString.isEmpty()) {
      return ""
    }

    if (apiString.isNotEmpty()) {
      return apiString
    }

    val resId = context.resources.getIdentifier(resString, "string", context.packageName)
    return context.resources.getString(resId)
  }
}