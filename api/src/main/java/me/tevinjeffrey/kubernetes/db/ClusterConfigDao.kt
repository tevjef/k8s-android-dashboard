package me.tevinjeffrey.kubernetes.db

import androidx.room.*
import io.reactivex.Flowable

interface ClusterConfigDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun setCurrentCluster(config: Config)

  @Query("SELECT * FROM config INNER JOIN cluster ON current_cluster_id = cluster_id")
  fun watchCurrentCluster(): Flowable<Cluster>

  @Query("SELECT * FROM config INNER JOIN cluster ON current_cluster_id = cluster_id")
  fun getCurrentCluster(): Cluster

  @Query("SELECT * FROM cluster")
  fun watchClusters(): Flowable<List<Cluster>>

  @Query("SELECT * FROM cluster")
  fun allClusters(): List<Cluster>

  @Insert
  fun addCluster(cluster: Cluster)

  @Delete
  fun deleteCluster(cluster: Cluster)

  @Query("SELECT * FROM cluster ORDER BY cluster_id DESC LIMIT 1")
  fun lastCluster(): Cluster

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateCluster(cluster: Cluster)
}