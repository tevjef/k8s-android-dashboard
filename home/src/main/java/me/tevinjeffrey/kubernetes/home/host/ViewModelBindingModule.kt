package me.tevinjeffrey.kubernetes.home.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.tevinjeffrey.kubernetes.base.di.ViewModelFactory
import me.tevinjeffrey.kubernetes.base.di.ViewModelKey
import me.tevinjeffrey.kubernetes.home.settings.AuthViewModel
import me.tevinjeffrey.kubernetes.home.settings.CertViewModel
import me.tevinjeffrey.kubernetes.home.settings.EndpointViewModel
import me.tevinjeffrey.kubernetes.home.settings.SettingsViewModel
import me.tevinjeffrey.kubernetes.home.workloads.WorkloadsViewModel

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

  @Binds
  @IntoMap
  @ViewModelKey(SettingsViewModel::class)
  abstract fun settingsViewModel(viewModel: SettingsViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(AuthViewModel::class)
  abstract fun authViewModel(viewModel: AuthViewModel): ViewModel


  @Binds
  @IntoMap
  @ViewModelKey(AuthViewModel::class)
  abstract fun workloadsViewModel(viewModel: WorkloadsViewModel): WorkloadsViewModel
}
