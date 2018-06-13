package me.tevinjeffrey.kubernetes.home

sealed class HomeEvent {
  object LoadHomeData : HomeEvent()
}
