package me.tevinjeffrey.kubernetes.entities.kubernetes.extensions

import io.fabric8.kubernetes.api.model.extensions.Deployment
import io.fabric8.kubernetes.api.model.extensions.DeploymentCondition
import me.tevinjeffrey.kubernetes.entities.kubernetes.DeploymentConditionType
import org.threeten.bp.ZonedDateTime

val Deployment.isOK: Boolean
  get() = {
    val conditions = this.status.conditions
    if (conditions.isNotEmpty()) {
      conditions.sortWith(Comparator<DeploymentCondition> { a, b ->
        if (a == null && b == null) {
          return@Comparator 0
        }

        val alast = ZonedDateTime.parse(a.lastUpdateTime)
        val blast = ZonedDateTime.parse(b.lastUpdateTime)
        blast.compareTo(alast)
      })
      conditions[0].type == DeploymentConditionType.DeploymentAvailable.value ||
      conditions[0].type == DeploymentConditionType.DeploymentProgressing.value
    }  else {
      false
    }
  }()