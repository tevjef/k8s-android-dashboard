package me.tevinjeffrey.kubernetes.db

enum class WorkloadSortKey {
  NAME,
  STATUS,
  TYPE,
  PODS,
  NAMESPACE,
  CLUSTER,
  PODS_RUNNING,
  PODS_DESIRED,
}