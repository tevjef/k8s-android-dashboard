package me.tevinjeffrey.kubernetes.home.workloads

import androidx.lifecycle.MutableLiveData
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import io.fabric8.kubernetes.api.model.Job
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.extensions.DaemonSet
import io.fabric8.kubernetes.api.model.extensions.Deployment
import io.fabric8.kubernetes.api.model.extensions.StatefulSet
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.api.KubernetesClientProvider
import me.tevinjeffrey.kubernetes.base.extensions.maybe
import me.tevinjeffrey.kubernetes.base.extensions.observable
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import me.tevinjeffrey.kubernetes.base.widgets.ResString
import me.tevinjeffrey.kubernetes.db.ConfigDatabase
import me.tevinjeffrey.kubernetes.db.WorkloadConfig
import me.tevinjeffrey.kubernetes.db.WorkloadType
import me.tevinjeffrey.kubernetes.db.WorkloadType.JOB
import me.tevinjeffrey.kubernetes.db.WorkloadType.DAEMON_SET
import me.tevinjeffrey.kubernetes.db.WorkloadType.DEPLOYMENT
import me.tevinjeffrey.kubernetes.db.WorkloadType.STATEFUL_SET
import me.tevinjeffrey.kubernetes.home.host.BaseKubernetesViewModel
import me.tevinjeffrey.kubernetes.home.workloads.adapter.NamespaceFilter
import me.tevinjeffrey.kubernetes.home.workloads.adapter.WorkloadTypeFilter
import timber.log.Timber
import javax.inject.Inject

class WorkloadsViewModel @Inject constructor(
    configDatabase: ConfigDatabase,
    clientProvider: KubernetesClientProvider
) : BaseKubernetesViewModel(configDatabase, clientProvider) {

  val deployments: MutableLiveData<List<Deployment>> = MutableLiveData()
  val statefulSets: MutableLiveData<List<StatefulSet>> = MutableLiveData()
  val daemonSets: MutableLiveData<List<DaemonSet>> = MutableLiveData()
  val cronJobs: MutableLiveData<List<Job>> = MutableLiveData()
  val searchQuery: MutableLiveData<String> = MutableLiveData()

  val isLoading: MutableLiveData<Boolean> = MutableLiveData()
  val namespaceData: MutableLiveData<NamespaceFilter> = MutableLiveData()
  val workloadTypeData: MutableLiveData<WorkloadTypeFilter> = MutableLiveData()

  private val configDao = configDatabase.configDao()

  init {
    configDao
        .watchWorkloadConfig()
        .doOnEach {
          isLoading.postValue(true)
        }
        .switchMap {
          listWorkloads()
              .toFlowable(BUFFER)
              .toList()
              .doOnEvent { _, _ ->
                isLoading.postValue(false)
              }
              .flattenAsFlowable { it }
        }
        .subscribeBy(
            onNext = {
              dispatchWorkload(it)
            },
            onError = { error.postValue(it) }
        )

    maybe {
      val currentWorkload = configDao.getWorkloadConfig()
      val selectedNamespaces = currentWorkload?.selectedNamespaces ?: emptySet()
      val namespaces = getClient().namespaces().list().items
      val chips = namespaces.map { it.metadata.name }
      NamespaceFilter(chips
          .map { ResString(apiString = it) }
          .toSet(),
          selectedNamespaces
              .map { ResString(apiString = it) }
              .toMutableSet()
      )
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { namespaceData.value = it },
            onError = { error.postValue(it) }
        )
    maybe {
      val currentWorkload = configDao.getWorkloadConfig()
      val selectedWorkloads = currentWorkload?.selectedWorkloads ?: emptySet()
      WorkloadTypeFilter(WorkloadType.values()
          .map { ResString(resString = it.value) }
          .toSet(), selectedWorkloads
          .map { ResString(resString = it.value) }
          .toMutableSet()
      )
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { workloadTypeData.value = it },
            onError = { error.postValue(it) }
        )
  }

  private fun dispatchWorkload(it: Workload) {
    when (it.type) {
      DEPLOYMENT -> deployments.value = it.items.map { it as Deployment }
      STATEFUL_SET -> statefulSets.value = it.items.map { it as StatefulSet }
      JOB -> cronJobs.value = it.items.map { it as Job }
      DAEMON_SET -> daemonSets.value = it.items.map { it as DaemonSet }
    }
  }

  fun updateSelectedNamespaces(namespaces: Set<ResString>) {
    maybe {
      val currentCluster = configDao.getCurrentCluster()
      var currentWorkload = configDao.getWorkloadConfig()
      if (currentWorkload == null) {
        val workloadConfig = WorkloadConfig(
            clusterId = currentCluster.clusterId,
            selectedNamespaces = namespaces.map { it.apiString }.toSet()
        )
        configDao.addWorkloadConfig(workloadConfig)
      } else {
        val workloadConfig = currentWorkload.copy(
            selectedNamespaces = namespaces.map { it.apiString }.toSet()
        )
        configDao.updateWorkloadConfig(workloadConfig)
      }
      currentWorkload = configDao.getWorkloadConfig()
      if (currentWorkload != null) {
        Some(currentWorkload)
      } else {
        None
      }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { },
            onError = { error.postValue(it) }
        )
  }

  fun updateSelectedWorkloads(workloadTypes: Set<ResString>) {
    maybe {
      val workloads = workloadTypes.map { type ->
        WorkloadType.values()
            .find { it.value == type.resString }!! }
          .toSet()

      val currentCluster = configDao.getCurrentCluster()
      var currentWorkload = configDao.getWorkloadConfig()
      if (currentWorkload == null) {
        val workloadConfig = WorkloadConfig(
            clusterId = currentCluster.clusterId,
            selectedWorkloads = workloads
        )
        configDao.addWorkloadConfig(workloadConfig)
      } else {
        val workloadConfig = currentWorkload.copy(
            selectedWorkloads = workloads
        )
        configDao.updateWorkloadConfig(workloadConfig)
      }
      currentWorkload = configDao.getWorkloadConfig()
      if (currentWorkload != null) {
        Some(currentWorkload)
      } else {
        None
      }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { },
            onError = { error.postValue(it) }
        )
  }

  fun listWorkloads(): Observable<Workload> {
    return observable {
      val currentWorkloadConfig = configDao.getWorkloadConfig()
      val namespaces = if (currentWorkloadConfig?.selectedNamespaces?.isNotEmpty() == true) {
        currentWorkloadConfig.selectedNamespaces
      } else {
        getClient().namespaces().list().items.map { it.metadata.name }
      }
      currentWorkloadConfig to namespaces
    }.flatMap { (workloadConfig, namespaces) ->

      val list = mutableListOf<ObservableSource<Workload>>()

      val isSelectedWorkloadEmpty = workloadConfig?.selectedWorkloads.orEmpty().isEmpty()

      fun addWorkload(type: WorkloadType, block:(String) -> List<HasMetadata>) {
        if (workloadConfig?.selectedWorkloads.orEmpty().contains(type) || isSelectedWorkloadEmpty) {
          list.add(observable {
            Workload(type, namespaces.flatMap {
              block(it)
            })
          })
        } else {
          list.add(observable {
            Workload(type, namespaces.flatMap {
              emptyList<HasMetadata>()
            })
          })
        }
      }

      addWorkload(DEPLOYMENT) {
        getClient().inNamespace(it).extensions().deployments().list().items
      }

      addWorkload(DAEMON_SET) {
        getClient().inNamespace(it).extensions().daemonSets().list().items
      }

      addWorkload(JOB) {
        getClient().inNamespace(it).extensions().jobs().list().items
      }

      addWorkload(STATEFUL_SET) {
        getClient().inNamespace(it).apps().statefulSets().list().items
      }
      Observable.merge(list.toList())
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun onRefresh() {
    listWorkloads()
        .doFinally {
          isLoading.value = false
        }
        .subscribeBy(
            onNext = {
              dispatchWorkload(it)
            },
            onError = { error.postValue(it) }
        )
  }

  fun resetNamespaceData() {
    namespaceData.value = null
  }

  fun resetWorkloadTypeData() {
    workloadTypeData.value = null
  }

  fun updateSearchQuery(query: String) {
    searchQuery.value = query
    deployments.value = deployments.value
    daemonSets.value = daemonSets.value
    statefulSets.value = statefulSets.value
    cronJobs.value = cronJobs.value
  }

  class Workload(val type: WorkloadType, val items: List<HasMetadata>)
}

