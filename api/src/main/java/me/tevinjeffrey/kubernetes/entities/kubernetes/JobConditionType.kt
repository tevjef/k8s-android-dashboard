package me.tevinjeffrey.kubernetes.entities.kubernetes

enum class JobConditionType(val value: String) {
  // JobComplete means the job has completed its execution.
  JobComplete("Complete"),
  // JobFailed means the job has failed its execution.
  JobFailed("Failed")
}