package me.tevinjeffrey.kubernetes.home.settings

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.api.ssl.CertUtils
import me.tevinjeffrey.kubernetes.base.extensions.maybe
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.FileOpener
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import me.tevinjeffrey.kubernetes.db.Cluster
import me.tevinjeffrey.kubernetes.db.ConfigDatabase
import me.tevinjeffrey.kubernetes.db.observeClusterValue
import me.tevinjeffrey.kubernetes.home.settings.adapter.CertInfo
import me.tevinjeffrey.kubernetes.home.settings.adapter.CertInfo.KeyInfo
import me.tevinjeffrey.kubernetes.home.settings.adapter.CertInfo.X509Info
import timber.log.Timber
import java.lang.IllegalStateException
import java.nio.charset.Charset
import javax.inject.Inject

class CertViewModel @Inject constructor(
    private val fileOpener: FileOpener,
    private val certUtils: CertUtils,
    private val configDatabase: ConfigDatabase
) : BaseViewModel() {

  val clientCertResult: MutableLiveData<CertInfo> = MutableLiveData()
  val clientKeyResult: MutableLiveData<CertInfo> = MutableLiveData()
  val clusterCACertResult: MutableLiveData<CertInfo> = MutableLiveData()
  val allowInsecureResult: MutableLiveData<Boolean> = MutableLiveData()

  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()

  init {
    observeCerts(clientCertResult, { it.clientCertificate })
    observeCerts(clientKeyResult, { it.clientKey })
    observeCerts(clusterCACertResult, { it.certificateAuthority })
    configDatabase.observeClusterValue(allowInsecureResult, { it.insecureSkipTLSVerify })
  }

  fun updateClientCert(uri: Uri) {
    updateCerts(uri) { cluster, certInfo ->
      cluster.copy(clientCertificate = certInfo.data)
    }
  }

  fun updateClientKey(uri: Uri) {
    updateCerts(uri) { cluster, certInfo ->
      cluster.copy(clientKey = certInfo.data)
    }
  }

  fun updateClusterCa(uri: Uri) {
    updateCerts(uri) { cluster, certInfo ->
      cluster.copy(certificateAuthority = certInfo.data)
    }
  }

  fun updateAllowInsecure(b: Boolean) {
    disposable += maybe {
      configDatabase.configDao().updateInsecureVerify(b)
    }
        .subscribe({ Timber.d("Updated insecureSkipTLSVerify=$b") }, { error.value = it })
  }

  private fun findFile(uri: Uri): Maybe<CertInfo> {
    return Maybe.create<CertInfo> {
      val source = fileOpener.openFile(uri)
      val data = source.readString(Charset.defaultCharset())
      source.close()

      val certInfo = try {
        getX509Info(data) ?: getKeyInfo(data)
      } catch (e: Exception) {
        null
      }

      if (certInfo != null) {
        it.onSuccess(certInfo)
      } else {
        it.onError(IllegalStateException("Please select a valid certificate."))
      }
    }
  }

  private fun updateCerts(uri: Uri, map: (Cluster, CertInfo) -> Cluster) {
    disposable += findFile(uri)
        .flatMap { certInfo ->
          maybe {
            val clusterConfig = configDatabase.configDao().getCurrentCluster()
            configDatabase.configDao().updateCluster(map(clusterConfig, certInfo))
            certInfo
          }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ Timber.d("Updated $it") }, { error.value = it })
  }

  private fun observeCerts(liveData: MutableLiveData<CertInfo>, keySelector: (Cluster) -> String?) {
    configDatabase.configDao().getDistinctCluster()
        .flatMap {
          val certInfo = getCertInfo(keySelector(it))
          certInfo ?: return@flatMap Flowable.just(None)
          Flowable.just(Some(certInfo))
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onNext = { liveData.value = it.toNullable() },
            onError = { Timber.e(it) }
        )
  }

  private fun getCertInfo(data: String?): CertInfo? = getX509Info(data) ?: getKeyInfo(data)

  private fun getX509Info(data: String?): X509Info? {
    data ?: return null
    if (data.isEmpty()) return null

    val certName = try {
      certUtils.getX509Certificates(certUtils.getInputStreamFromDataOrFile(data, null))
          .joinToString { it.issuerX500Principal.name }
    } catch (e: Exception) {
      null
    }

    certName ?: return null
    if (certName.isEmpty()) return null

    return X509Info(data, certName)
  }

  private fun getKeyInfo(data: String?): KeyInfo? {
    data ?: return null
    if (data.isEmpty()) return null

    val privateKey = try {
      certUtils.getPrivateKey(certUtils.getInputStreamFromDataOrFile(data, null))
    } catch (e: Exception) {
      Timber.e(e)
      null
    }

    privateKey ?: return null

    return KeyInfo(data, privateKey.format, privateKey.algorithm)
  }
}
