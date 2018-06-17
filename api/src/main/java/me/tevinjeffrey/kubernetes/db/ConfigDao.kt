package me.tevinjeffrey.kubernetes.db

import androidx.room.*
import io.reactivex.Flowable

@Dao
abstract class ConfigDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun setCurrentCluster(config: Config)

  @Query("SELECT * FROM config INNER JOIN cluster ON current_cluster_id = cluster_id")
  abstract fun watchCurrentCluster(): Flowable<Cluster>

  @Query("SELECT * FROM config INNER JOIN cluster ON current_cluster_id = cluster_id")
  abstract fun getCurrentCluster(): Cluster


  fun getDistinctCluster(): Flowable<Cluster> = watchCurrentCluster()
      .distinctUntilChanged()

  @Query("SELECT * FROM cluster")
  abstract fun watchClusters(): Flowable<List<Cluster>>

  @Query("SELECT * FROM cluster")
  abstract fun allClusters(): List<Cluster>

  @Insert
  abstract fun addCluster(cluster: Cluster)

  @Delete
  abstract fun deleteCluster(cluster: Cluster)

  @Query("SELECT * FROM cluster ORDER BY cluster_id DESC LIMIT 1")
  abstract fun lastCluster(): Cluster

  @Update(onConflict = OnConflictStrategy.REPLACE)
  abstract fun updateCluster(cluster: Cluster)

  @Transaction
  open fun prepopulate(config: Config, cluster: Cluster) {
    addCluster(cluster)
    setCurrentCluster(config)
  }

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
}