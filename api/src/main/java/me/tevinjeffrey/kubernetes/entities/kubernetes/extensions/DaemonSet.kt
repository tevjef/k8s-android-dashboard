package me.tevinjeffrey.kubernetes.entities.kubernetes.extensions

import io.fabric8.kubernetes.api.model.extensions.DaemonSet
import io.fabric8.kubernetes.api.model.extensions.Deployment
import me.tevinjeffrey.kubernetes.entities.kubernetes.DeploymentConditionType

val DaemonSet.isOK: Boolean
  get() = this.status.currentNumberScheduled == this.status.desiredNumberScheduled