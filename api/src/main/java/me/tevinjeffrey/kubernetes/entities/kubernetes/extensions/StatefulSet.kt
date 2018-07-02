package me.tevinjeffrey.kubernetes.entities.kubernetes.extensions

import io.fabric8.kubernetes.api.model.extensions.StatefulSet

val StatefulSet.isOK: Boolean
  get() = this.status.readyReplicas == this.status.replicas