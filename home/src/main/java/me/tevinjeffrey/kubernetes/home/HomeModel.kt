package me.tevinjeffrey.kubernetes.home

sealed class HomeModel {
  class HomeProgress(val isLoading: Boolean) : HomeModel()
  data class HomeSuccess(val items: List<String>) : HomeModel()
  class HomeFailure(val error: Throwable) : HomeModel()
}
