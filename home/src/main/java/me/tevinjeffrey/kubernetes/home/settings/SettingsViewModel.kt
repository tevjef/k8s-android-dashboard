package me.tevinjeffrey.kubernetes.home.settings

import androidx.lifecycle.MutableLiveData
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import io.fabric8.kubernetes.api.model.RootPaths
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientException
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.api.KubernetesClientProvider
import me.tevinjeffrey.kubernetes.base.extensions.maybe
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import me.tevinjeffrey.kubernetes.db.Cluster
import me.tevinjeffrey.kubernetes.db.Config
import me.tevinjeffrey.kubernetes.db.ConfigDatabase
import me.tevinjeffrey.kubernetes.home.settings.adapter.SpinnerDisplay
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

class SettingsViewModel @Inject constructor(
    private val kubernetesClientProvider: KubernetesClientProvider,
    configDatabase: ConfigDatabase
    ) : BaseViewModel() {

  val spinnerLiveData: MutableLiveData<SpinnerDisplay> = MutableLiveData()
  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()
  val message: SingleLiveEvent<String> = SingleLiveEvent()
  private val configDao = configDatabase.configDao()

  init {
    Observable.combineLatest(
        configDao
            .watchClusters()
            .toObservable(),
        configDao
            .watchCurrentCluster()
            .toObservable(),
        BiFunction { t1: List<Cluster>, t2: Cluster -> SpinnerDisplay(t2, t1) }
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onNext = { spinnerLiveData.value = it },
            onError = { error.value = it }
        )
  }

  fun checkConnection() {
    disposable += maybe { configDao.getCurrentCluster() }
        .flatMap { config ->
          if (config.server.isNullOrEmpty()) {
            Maybe.error<Optional<RootPaths>>(KubernetesClientException("Kubernetes Master URL is empty."))
          } else {
            maybe {
              val client: DefaultKubernetesClient = kubernetesClientProvider.get(config)
              val paths = client.inAnyNamespace().rootPaths()

              if (paths != null && paths.paths.isNotEmpty()) {
                Some(paths)
              } else {
                None
              }
            }
          }
        }
        .flatMap {
          maybe {
            if (it.toNullable() == null) {
              throw KubernetesClientException("Connection failed")
            } else {
              it.toNullable()!!
            }

          }
        }
        .subscribeBy(
            onSuccess = {
              message.value = "Connection successful"
            },
            onError = {
              Timber.e(it)
              val e = if (it.cause != null) it.cause else it
              if (e is SSLHandshakeException) {
                error.value = KubernetesClientException("Could not establish a secure connection to the Kubernetes master.", e)
              } else if (e is KubernetesClientException && e.status != null) {
                error.value = KubernetesClientException(e.status.message, e)
              } else {
                error.value = e
              }
            }
        )
  }

  fun deleteCurrentConfig() {
    disposable += maybe {
      val allClusters = configDao.allClusters()
      if (allClusters.size == 1) {
        return@maybe false to null
      }
      val currentCluster = configDao.getCurrentCluster()
      configDao.deleteCluster(currentCluster)
      val lastCluster = configDao.lastCluster()
      configDao.setCurrentCluster(Config(currentClusterId = lastCluster.clusterId))
      true to currentCluster
    }
        .subscribeBy(
            onSuccess = { },
            onError = { error.value = it }
        )
  }

  fun setCurrentCluster(id: Int) {
    disposable += maybe {
      configDao.setCurrentCluster(Config(currentClusterId = id))
      configDao.getCurrentCluster()
    }
        .subscribeBy(
            onSuccess = { },
            onError = { error.value = it }
        )
  }

  fun newClusterConfig() {
    disposable += maybe {
      var lastCluster = configDao.lastCluster()
      configDao.addCluster(Cluster(name = "New Cluster ${lastCluster.clusterId}"))
      lastCluster = configDao.lastCluster()
      configDao.setCurrentCluster(Config(currentClusterId = lastCluster.clusterId))
      configDao.getCurrentCluster()
    }
        .subscribeBy(
            onSuccess = { },
            onError = { error.value = it }
        )
  }

  fun updateConfigName(name: String) {
    disposable += maybe { configDao.updateName(name) }
        .subscribeBy(
            onSuccess = { },
            onError = { error.value = it }
        )
  }

  fun resetCurrentConfig() {
    disposable += maybe {
      val currentConfig = configDao.getCurrentCluster()
      val newCluster = Cluster(clusterId = currentConfig.clusterId, name = currentConfig.name)
      configDao.updateCluster(newCluster)
      configDao.getCurrentCluster()
    }
        .subscribeBy(
            onSuccess = { },
            onError = { error.value = it }
        )
  }
}

