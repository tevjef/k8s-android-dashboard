package me.tevinjeffrey.kubernetes.entities.kubernetes

enum class DeploymentConditionType(val value: String) {

  // Available means the deployment is available, ie. at least the minimum available
  // replicas required are up and running for at least minReadySeconds.
  DeploymentAvailable("Available"),
  // Progressing means the deployment is progressing. Progress for a deployment is
  // considered when a new replica set is created or adopted, and when new pods scale
  // up or old pods scale down. Progress is not estimated for paused deployments or
  // when progressDeadlineSeconds is not specified.
  DeploymentProgressing("Progressing"),
  // ReplicaFailure is added in a deployment when one of its pods fails to be created
  // or deleted.
  DeploymentReplicaFailure("ReplicaFailure")
}