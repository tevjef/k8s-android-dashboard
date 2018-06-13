package me.tevinjeffrey.kubernetes.home.setup.cert

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.net.Uri
import com.prolificinteractive.patrons.StringPreference
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.base.di.ClientCert
import me.tevinjeffrey.kubernetes.base.di.ClientKey
import me.tevinjeffrey.kubernetes.base.di.ClusterCACert
import okio.Okio
import javax.inject.Inject

class CertViewModel @Inject constructor(
    @ClientCert private val clientCert: StringPreference,
    @ClientKey private val clientKey: StringPreference,
    @ClusterCACert private val clusterCACert: StringPreference
) : ViewModel() {

  val clientCertResult: MutableLiveData<Boolean> = MutableLiveData()
  val clientKeyResult: MutableLiveData<Boolean> = MutableLiveData()
  val clusterCACertResult: MutableLiveData<Boolean> = MutableLiveData()

  private val error: MutableLiveData<Throwable> = MutableLiveData()

  fun updateClientCert(context: Context, uri: Uri) {
    val d = findFile(context, uri)
        .subscribe({
          clientCert.set(uri.path)
          clientCertResult.value = it
        }, { error.value = it })
  }

  fun updateClientKey(context: Context, uri: Uri) {
    val d = findFile(context, uri)
        .subscribe({
          clientKey.set(uri.path)
          clientKeyResult.value = it
        }, { error.value = it })
  }

  fun updateClusterCa(context: Context, uri: Uri) {
    val d = findFile(context, uri)
        .subscribe({
          clusterCACert.set(uri.path)
          clusterCACertResult.value = it
        }, { error.value = it })
  }

  private fun findFile(context: Context, uri: Uri): Maybe<Boolean> {
    return Maybe.create<Boolean> {
      val source = Okio.buffer(Okio.source(context.contentResolver.openInputStream(uri)))
      val isCert = source.readUtf8Line().orEmpty().contains("-----BEGIN")
      source.close()
      it.onSuccess(isCert)
    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }
}
