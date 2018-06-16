package me.tevinjeffrey.kubernetes.home.settings

import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import com.prolificinteractive.patrons.BooleanPreference
import com.prolificinteractive.patrons.OnPreferenceChangeListener
import com.prolificinteractive.patrons.StringPreference
import me.tevinjeffrey.kubernetes.base.di.AllowInsecure
import me.tevinjeffrey.kubernetes.base.di.MasterUrl
import me.tevinjeffrey.kubernetes.base.di.ProxyUrl
import me.tevinjeffrey.kubernetes.base.di.ShouldProxy
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import javax.inject.Inject

class EndpointViewModel @Inject constructor(
    @MasterUrl private val masterUrl: StringPreference,
    @ProxyUrl private val proxyUrl: StringPreference,
    @ShouldProxy private val shouldProxy: BooleanPreference,
    @AllowInsecure private val allowInsecure: BooleanPreference
) : BaseViewModel() {

  val masterUrlResult: MutableLiveData<String> = MutableLiveData()
  val proxyUrlResult: MutableLiveData<String> = MutableLiveData()
  val shouldProxyResult: MutableLiveData<Boolean> = MutableLiveData()

  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()

  init {
    masterUrl.registerChangeListener(OnPreferenceChangeListener {
      masterUrlResult.value = it
    })
    proxyUrl.registerChangeListener(OnPreferenceChangeListener {
      proxyUrlResult.value = it
    })
    shouldProxy.registerChangeListener(OnPreferenceChangeListener {
      shouldProxyResult.value = it
    })
    allowInsecure.registerChangeListener(OnPreferenceChangeListener {
      toggleMasterSecureUrl(!(it ?: false))
    })

    masterUrlResult.value = masterUrl.get()
    proxyUrlResult.value = proxyUrl.get()
    shouldProxyResult.value = shouldProxy.get()
    toggleMasterSecureUrl(!(allowInsecure.get()))
  }

  fun updateMasterUrl(text: String) {
    val isHttp = URLUtil.isHttpUrl(text)
    val isHttps = URLUtil.isHttpsUrl(text)

    if (isHttps) {
      allowInsecure.set(false)
    }

    if (isHttp) {
      allowInsecure.set(true)
    }

    if (isHttp || isHttps) {
      masterUrl.set(text)
    } else {
      error.value = IllegalStateException("Invalid master url ")
    }
  }

  fun updateProxyUrl(text: String) {
    val isValidUrl = URLUtil.isValidUrl(text)
    val isHttps = URLUtil.isHttpsUrl(text)
    when {
      isHttps -> error.value = IllegalStateException("https proxy not supported")
      isValidUrl -> {
        proxyUrl.set(text)
      }
      else -> error.value = IllegalStateException("Invalid proxy url ")
    }
  }

  fun updateShouldProxy(b: Boolean) {
    shouldProxy.set(b)
  }

  private fun toggleMasterSecureUrl(isSecure: Boolean) {
    val url = masterUrl.get()
    url ?: return

    val uri = Uri.parse(url)
    val newUrl = uri.buildUpon()
        .scheme(if (isSecure) "https" else "http")
        .build()
    masterUrl.set(newUrl.toString())
  }
}


