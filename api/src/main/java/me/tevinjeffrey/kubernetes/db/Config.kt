package me.tevinjeffrey.kubernetes.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Config is a tuple of references to a cluster (how do I communicate with a kubernetes cluster),
 * a user (how do I identify myself), and a namespace (what subset of resources do I want to work with)
 */
@Entity(indices = [(Index("current_cluster_id"))])
data class Config(
  @PrimaryKey
  val configId: Int = 1,

  /**
   * CurrentContext is the name of the context that you would like to use by default.
   */
  @ColumnInfo(name = "current_cluster_id")
  val currentClusterId: Int
)