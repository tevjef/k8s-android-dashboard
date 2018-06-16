package me.tevinjeffrey.kubernetes.home.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.tevinjeffrey.kubernetes.base.di.ViewModelFactory
import me.tevinjeffrey.kubernetes.base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.tevinjeffrey.kubernetes.home.settings.CertViewModel
import me.tevinjeffrey.kubernetes.home.settings.EndpointViewModel

@Module
abstract class ViewModelBindingModule {

  @Binds
  abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

  @Binds
  @IntoMap
  @ViewModelKey(CertViewModel::class)
  abstract fun certViewModel(viewModel: CertViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(EndpointViewModel::class)
  abstract fun endpointViewModel(viewModel: EndpointViewModel): ViewModel
}
