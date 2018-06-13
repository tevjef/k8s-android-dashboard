package me.tevinjeffrey.kubernetes.home.host

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import me.tevinjeffrey.kubernetes.base.di.ViewModelFactory
import me.tevinjeffrey.kubernetes.base.di.ViewModelKey
import me.tevinjeffrey.kubernetes.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelBindingModule {

  @Binds
  abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

  @Binds
  @IntoMap
  @ViewModelKey(HomeViewModel::class)
  abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel
}
