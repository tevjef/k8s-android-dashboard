package me.tevinjeffrey.kubernetes.home.settings

import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import me.tevinjeffrey.kubernetes.base.extensions.maybe
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import me.tevinjeffrey.kubernetes.db.ConfigDatabase
import me.tevinjeffrey.kubernetes.db.observeClusterValue
import javax.inject.Inject

class EndpointViewModel @Inject constructor(configDatabase: ConfigDatabase) : BaseViewModel() {
  val masterUrlResult: MutableLiveData<String> = MutableLiveData()
  val proxyUrlResult: MutableLiveData<String> = MutableLiveData()
  val shouldProxyResult: MutableLiveData<Boolean> = MutableLiveData()
  val allowInsecureResult: MutableLiveData<Boolean> = MutableLiveData()

  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()

  private val configDao = configDatabase.configDao()

  init {
    configDatabase.observeClusterValue(shouldProxyResult, { it.shouldProxy })
    configDatabase.observeClusterValue(proxyUrlResult, { it.proxyUrl })
    configDatabase.observeClusterValue(masterUrlResult, { it.server })
    configDatabase.observeClusterValue(allowInsecureResult, { it.insecureSkipTLSVerify })
    configDatabase.observeClusterValue(
        { toggleMasterSecureUrl(!(it ?: false)) },
        { it.insecureSkipTLSVerify }
    )
  }

  fun updateMasterUrl(text: String) {
    val isHttp = URLUtil.isHttpUrl(text)
    val isHttps = URLUtil.isHttpsUrl(text)

    var allowInsecure = false
    if (isHttps) {
      allowInsecure = false
    }

    if (isHttp) {
      allowInsecure = true
    }

    disposable +=
        maybe { configDao.updateInsecureVerify(allowInsecure) }
            .subscribeBy(
                onSuccess = { },
                onError = { error.value = it }
            )
    if (isHttp || isHttps) {
      disposable += maybe { configDao.updateMasterUrl(text) }
          .subscribeBy(
              onSuccess = { },
              onError = { error.value = it }
          )
    } else {
      error.value = IllegalStateException("Invalid URL")
    }
  }

  fun updateProxyUrl(text: String) {
    val isValidUrl = URLUtil.isValidUrl(text)
    when {
      isValidUrl -> {
        maybe { configDao.updateProxyUrl(text) }
            .subscribeBy(
                onSuccess = { },
                onError = { error.value = it }
            )
      }
      else -> error.value = IllegalStateException("Invalid proxy url ")
    }
  }

  fun updateShouldProxy(shouldProxy: Boolean) {
    disposable += maybe { configDao.updateShouldProxy(shouldProxy) }
        .subscribeBy(
            onSuccess = { },
            onError = { error.value = it }
        )
  }

  private fun toggleMasterSecureUrl(isSecure: Boolean) {
    val url = masterUrlResult.value
    url ?: return

    val uri = Uri.parse(url)
    val newUrl = uri.buildUpon()
        .scheme(if (isSecure) "https" else "http")
        .build()

    disposable += maybe { configDao.updateMasterUrl(newUrl.toString()) }
        .subscribeBy(
            onSuccess = { },
            onError = { error.value = it }
        )
  }
}


