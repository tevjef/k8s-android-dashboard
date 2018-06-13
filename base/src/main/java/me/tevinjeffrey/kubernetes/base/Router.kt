package me.tevinjeffrey.kubernetes.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import okhttp3.HttpUrl
import javax.inject.Inject

class Router @Inject constructor(private val activity: Activity) {

  private val resources get() = activity.resources

  fun home() {
    activity.finishAffinity()
    activity.startActivity(intentForPath(setOf(resources.getString(R.string.path_home))))
  }

  fun login() {
    activity.startActivity(intentForPath(setOf(resources.getString(R.string.path_login))))
  }

  private fun intentForPath(
      path: Set<String> = emptySet(),
      queries: Map<String, String> = emptyMap()): Intent {
    val builder = HttpUrl.Builder()
        .scheme(resources.getString(R.string.scheme))
        .host(resources.getString(R.string.host))

    // need to strip off the leading / needed for Manifest.xml for first path segment
    path.forEach { builder.addPathSegment(it.removePrefix("/")) }
    queries.forEach { builder.addQueryParameter(it.key, it.value) }
    val url = builder.build()

    return Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())).apply {
      `package` = activity.packageName
      addCategory(Intent.CATEGORY_BROWSABLE)
    }
  }
}
