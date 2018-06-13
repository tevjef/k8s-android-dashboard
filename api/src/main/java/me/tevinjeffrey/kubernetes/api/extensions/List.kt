package me.tevinjeffrey.kubernetes.api.extensions

inline fun <T> List<T>.indexOrNull(predicate: (T) -> Boolean): Int? {
  for ((index, item) in this.withIndex()) {
    if (predicate(item))
      return index
  }
  return null
}
