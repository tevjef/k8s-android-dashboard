package me.tevinjeffrey.kubernetes.base.support

import android.content.Context
import android.net.Uri
import okio.BufferedSource
import okio.Okio

interface FileOpener {
  fun openFile(uri: Uri): BufferedSource
}

class AndroidFileOpener(private val context: Context) : FileOpener {
  override fun openFile(uri: Uri) = Okio.buffer(Okio.source(context.contentResolver.openInputStream(uri)))!!
}