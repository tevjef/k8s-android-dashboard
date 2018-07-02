package me.tevinjeffrey.kubernetes.db

import androidx.room.Dao
import androidx.room.Transaction
import io.reactivex.Flowable

@Dao
abstract class ConfigDao : ClusterConfigDao, WorkloadConfigDao {

  fun getDistinctCluster(): Flowable<Cluster> = watchCurrentCluster()
      .distinctUntilChanged()

  fun updateMasterUrl(masterUrl: String?) = updateCluster {
    it.copy(server = masterUrl)
  }

  fun updateShouldProxy(shouldProxy: Boolean) = updateCluster {
    it.copy(shouldProxy = shouldProxy)
  }

  fun updateProxyUrl(proxyUrl: String?) = updateCluster {
    it.copy(proxyUrl = proxyUrl)
  }

  fun updateInsecureVerify(allowInsecure: Boolean) = updateCluster {
    it.copy(insecureSkipTLSVerify = allowInsecure)
  }

  fun updateClientCertficate(data: String?) = updateCluster {
    it.copy(clientCertificate = data)
  }

  fun updateClientKey(data: String?) = updateCluster {
    it.copy(clientKey = data)
  }

  fun updateCertificateAuthority(data: String?) = updateCluster {
    it.copy(certificateAuthority = data)
  }

  fun updateName(name: String?) = updateCluster {
    it.copy(name = name)
  }

  fun updateToken(token: String?) = updateCluster {
    it.copy(token = token)
  }

  fun updateUsername(name: String?) = updateCluster {
    it.copy(username = name)
  }

  fun updatePassword(password: String?) = updateCluster {
    it.copy(password = password)
  }

  private inline fun updateCluster(map: (Cluster) -> Cluster): Cluster {
    val currentCluster = getCurrentCluster()
    val newCluster = map(currentCluster)
    updateCluster(newCluster)
    return newCluster
  }

  @Transaction
  open fun prepopulate(config: Config, cluster: Cluster) {
    addCluster(cluster)
    setCurrentCluster(config)
  }

}