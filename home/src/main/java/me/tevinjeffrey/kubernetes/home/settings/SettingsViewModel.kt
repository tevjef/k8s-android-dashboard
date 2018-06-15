package me.tevinjeffrey.kubernetes.home.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.prolificinteractive.patrons.StringPreference
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.base.di.ClientCert
import me.tevinjeffrey.kubernetes.base.di.ClientKey
import me.tevinjeffrey.kubernetes.base.di.ClusterCACert
import me.tevinjeffrey.kubernetes.base.extensions.plusAssign
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import okio.Okio
import java.io.File
import java.lang.IllegalStateException
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    @ClientCert private val clientCert: StringPreference,
    @ClientKey private val clientKey: StringPreference,
    @ClusterCACert private val clusterCACert: StringPreference
) : BaseViewModel() {

  val clientCertResult: MutableLiveData<FileInfo> = MutableLiveData()
  val clientKeyResult: MutableLiveData<FileInfo> = MutableLiveData()
  val clusterCACertResult: MutableLiveData<FileInfo> = MutableLiveData()

  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()

  fun loadData() {
    clientCertResult.value = getFileInfo(clientCert.get())
    clientKeyResult.value = getFileInfo(clientKey.get())
    clusterCACertResult.value = getFileInfo(clusterCACert.get())
  }

  fun updateClientCert(context: Context, uri: Uri) {
    disposable += findFile(context, uri)
        .subscribe({
          clientCert.set(uri.path)
          clientCertResult.value = it
        }, { error.value = it })
  }

  fun updateClientKey(context: Context, uri: Uri) {
    disposable += findFile(context, uri)
        .subscribe({
          clientKey.set(uri.path)
          clientKeyResult.value = it
        }, { error.value = it })
  }

  fun updateClusterCa(context: Context, uri: Uri) {
    disposable += findFile(context, uri)
        .subscribe({
          clusterCACert.set(uri.path)
          clusterCACertResult.value = it
        }, { error.value = it })
  }

  private fun findFile(context: Context, uri: Uri): Maybe<FileInfo> {
    return Maybe.create<FileInfo> {
      val source = Okio.buffer(Okio.source(context.contentResolver.openInputStream(uri)))
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


