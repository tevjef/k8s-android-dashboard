package me.tevinjeffrey.kubernetes.home

import com.xwray.groupie.Group
import com.xwray.groupie.Item
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.ViewHolder

fun NestedGroup.removeAll() {
  this.removeAll(allItems)
}

val Group.allItems: List<Item<out ViewHolder>>
  get() = {
    (0 until this.itemCount).map {
      this.getItem(it)
    }
  }()