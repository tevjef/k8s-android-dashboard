package me.tevinjeffrey.kubernetes.home.settings

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.prolificinteractive.patrons.BooleanPreference
import com.prolificinteractive.patrons.OnPreferenceChangeListener
import com.prolificinteractive.patrons.StringPreference
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.base.di.AllowInsecure
import me.tevinjeffrey.kubernetes.base.di.ClientCert
import me.tevinjeffrey.kubernetes.base.di.ClientKey
import me.tevinjeffrey.kubernetes.base.di.ClusterCACert
import me.tevinjeffrey.kubernetes.base.extensions.plusAssign
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.FileOpener
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import java.io.File
import java.lang.IllegalStateException
import javax.inject.Inject

class CertViewModel @Inject constructor(
    private val fileOpener: FileOpener,
    @ClientCert private val clientCert: StringPreference,
    @ClientKey private val clientKey: StringPreference,
    @ClusterCACert private val clusterCACert: StringPreference,
    @AllowInsecure private val allowInsecure: BooleanPreference
) : BaseViewModel() {

  val clientCertResult: MutableLiveData<FileInfo> = MutableLiveData()
  val clientKeyResult: MutableLiveData<FileInfo> = MutableLiveData()
  val clusterCACertResult: MutableLiveData<FileInfo> = MutableLiveData()
  val allowInsecureResult: MutableLiveData<Boolean> = MutableLiveData()

  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()

  init {
    clientCert.registerChangeListener(OnPreferenceChangeListener {
      clientCertResult.value = getFileInfo(it)
    })
    clientKey.registerChangeListener(OnPreferenceChangeListener {
      clientKeyResult.value = getFileInfo(it)
    })
    clusterCACert.registerChangeListener(OnPreferenceChangeListener {
      clusterCACertResult.value = getFileInfo(it)
    })
    allowInsecure.registerChangeListener(OnPreferenceChangeListener {
      allowInsecureResult.value = it
    })
    clientCertResult.value = getFileInfo(clientCert.get())
    clientKeyResult.value = getFileInfo(clientKey.get())
    clusterCACertResult.value = getFileInfo(clusterCACert.get())
    allowInsecureResult.value = allowInsecure.get()
  }

  fun updateClientCert(uri: Uri) {
    disposable += findFile(uri)
        .subscribe({
          clientCert.set(uri.path)
        }, { error.value = it })
  }

  fun updateClientKey(uri: Uri) {
    disposable += findFile(uri)
        .subscribe({
          clientKey.set(uri.path)
        }, { error.value = it })
  }

  fun updateClusterCa(uri: Uri) {
    disposable += findFile(uri)
        .subscribe({
          clusterCACert.set(uri.path)
        }, { error.value = it })
  }

  fun updateAllowInsecure(b: Boolean) {
    allowInsecure.set(b)
  }

  private fun findFile(uri: Uri): Maybe<FileInfo> {
    return Maybe.create<FileInfo> {
      val source = fileOpener.openFile(uri)
      val isCert = source.readUtf8Line().orEmpty().contains("-----BEGIN")
      source.close()

      if (isCert) {
        it.onSuccess(getFileInfo(uri.path)!!)
      } else {
        it.onError(IllegalStateException("Please select a valid certificate."))
      }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  private fun getFileInfo(uri: String?): FileInfo? {
    uri ?: return null
    val file = File(uri)
    return FileInfo(file.name, file.canonicalPath)
  }
}


