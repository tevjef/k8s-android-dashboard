package me.tevinjeffrey.kubernetes.home.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.tevinjeffrey.kubernetes.base.di.ViewModelFactory
import me.tevinjeffrey.kubernetes.base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.tevinjeffrey.kubernetes.home.settings.SettingsViewModel

@Module
abstract class ViewModelBindingModule {

  @Binds
  abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

  @Binds
  @IntoMap
  @ViewModelKey(SettingsViewModel::class)
  abstract fun settingsViewModel(viewModel: SettingsViewModel): ViewModel
}
