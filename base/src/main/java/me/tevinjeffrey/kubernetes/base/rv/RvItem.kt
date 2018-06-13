package me.tevinjeffrey.kubernetes.base.rv

/**
 * A typical implementation of [RvItem] would override the [itemViewType(): Int] method to use an
 * int unique to the [RvAdapter]. That method would be called in the adapter in the
 * [RecyclerView.Adapter.getItemViewType()] method.
 */
interface RvItem {
  fun itemViewType(): Int = 0
  fun gridSpan(): Int = 1
}
