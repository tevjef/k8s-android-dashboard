package me.tevinjeffrey.kubernetes.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable

interface WorkloadConfigDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun addWorkloadConfig(config: WorkloadConfig)

  @Query("SELECT * FROM workload_config INNER JOIN config ON current_cluster_id = cluster_id")
  fun getWorkloadConfig(): WorkloadConfig?

  @Query("SELECT * FROM workload_config INNER JOIN config ON current_cluster_id = cluster_id")
  fun watchWorkloadConfig(): Flowable<WorkloadConfig>

  @Delete
  fun deleteWorkloadConfig(config: WorkloadConfig)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateWorkloadConfig(config: WorkloadConfig)
}