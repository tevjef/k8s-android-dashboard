package me.tevinjeffrey.kubernetes.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workload_config",
    indices = [Index("_id", "cluster_id")]
)
data class WorkloadConfig(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val workload_config_id: Int = 0,

    @ColumnInfo(name = "cluster_id")
    val clusterId: Int,

    @ColumnInfo(name = "selected_namespaces")
    val selectedNamespaces: Set<String> = setOf("default"),

    @ColumnInfo(name = "selected_workloads")
    val selectedWorkloads: Set<WorkloadType> = emptySet(),

    @ColumnInfo(name = "active_key")
    val activeKey: WorkloadSortKey = WorkloadSortKey.NAME,

    @ColumnInfo(name = "ascending")
    val ascending: Boolean = false
)