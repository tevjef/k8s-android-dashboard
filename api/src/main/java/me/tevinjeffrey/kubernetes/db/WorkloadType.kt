package me.tevinjeffrey.kubernetes.db

enum class WorkloadType(val value: String) {
  DEPLOYMENT("deployments"),
  STATEFUL_SET("stateful_sets"),
  DAEMON_SET("daemon_sets"),
  JOB("jobs"),
}