package me.tevinjeffrey.kubernetes.home.host

import androidx.lifecycle.MutableLiveData
import io.fabric8.kubernetes.api.model.extensions.Deployment
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.api.KubernetesClientProvider
import me.tevinjeffrey.kubernetes.base.extensions.maybe
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import me.tevinjeffrey.kubernetes.db.Cluster
import me.tevinjeffrey.kubernetes.db.ConfigDatabase

open class BaseKubernetesViewModel(
    configDatabase: ConfigDatabase,
    private val kubernetesClientProvider: KubernetesClientProvider
) : BaseViewModel() {

  internal val currentCluster: MutableLiveData<Cluster> = MutableLiveData()
  internal val kubernetesClient: MutableLiveData<DefaultKubernetesClient> = MutableLiveData()

  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()
  val message: SingleLiveEvent<String> = SingleLiveEvent()
  private val configDao = configDatabase.configDao()

  init {
    configDao.watchCurrentCluster()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onNext = {
              kubernetesClient.postValue(kubernetesClientProvider.get(it))
              currentCluster.value = it

            },
            onError = { error.value = it }
        )
  }

  fun getClient(): DefaultKubernetesClient {
    val currentConfig = configDao.getCurrentCluster()
    val client = kubernetesClientProvider.get(currentConfig)
    kubernetesClient.postValue(client)
    return client
  }

  fun deploymentList(): Maybe<List<Deployment>> {
    return maybe {
      val currentWorkloadConfig = configDao.getWorkloadConfig()
      val namespaces = if (currentWorkloadConfig?.selectedNamespaces?.isNotEmpty() == true) {
        currentWorkloadConfig.selectedNamespaces
      } else {
        getClient().namespaces().list().items.map { it.metadata.name }
      }

      val deployments = namespaces.flatMap {
        getClient().inNamespace(it).extensions().deployments().list().items
      }

      val deamonSets = namespaces.flatMap {
        getClient().inNamespace(it).extensions().daemonSets().list().items
      }

      val jobs = namespaces.flatMap {
        getClient().inNamespace(it).extensions().jobs().list().items
      }

      val statefulSets = namespaces.flatMap {
        getClient().inNamespace(it).apps().statefulSets().list().items
      }
      deployments
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }
}

