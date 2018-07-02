package me.tevinjeffrey.kubernetes.entities.kubernetes.extensions

import io.fabric8.kubernetes.api.model.Job
import me.tevinjeffrey.kubernetes.entities.kubernetes.JobConditionType

val Job.isOK: Boolean
  get() = {
    val conditions = this.status.conditions
    if (conditions.isNotEmpty()) {
      conditions[0].type == JobConditionType.JobComplete.value
    } else {
      false
    }
  }()