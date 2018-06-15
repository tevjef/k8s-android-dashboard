package me.tevinjeffrey.kubernetes.base.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * [RvDelegate] is a generic implementation of [RecyclerView.ViewHolder] that has the type of
 * [RvItem] it will bind to your provided layout. The [create] and [bind] methods should seem
 * familiar as they do map directly to [RecyclerView.Adapter]'s [RecyclerView.Adapter.onCreateViewHolder] and
 * [RecyclerView.Adapter.onBindViewHolder] methods respectively.
 */
interface RvDelegate<in E, V : RecyclerView.ViewHolder> {
  @LayoutRes
  fun layoutId(): Int

  fun bind(item: E, holder: V)
  fun bind(item: E, holder: V, payloads: List<Any>) {
    bind(item, holder)
  }

  fun create(parent: ViewGroup): V

  fun itemView(parent: ViewGroup): View =
      LayoutInflater
          .from(parent.context)
          .inflate(layoutId(), parent, false)
}
