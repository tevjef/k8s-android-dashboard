package me.tevinjeffrey.kubernetes.base.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun View.showKeyboard(imm: InputMethodManager) {
  requestFocus()
  imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.hideKeyboard() {
  val focused = currentFocus
  if (focused != null) {
    focused.clearFocus()
    (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
        as InputMethodManager).hideSoftInputFromWindow(focused.windowToken, 0)
  }
}

fun Fragment.hideKeyboard() {
  activity!!.hideKeyboard()
}

fun View.hideKeyboard(imm: InputMethodManager) {
  imm.hideSoftInputFromWindow(windowToken, 0)
}


