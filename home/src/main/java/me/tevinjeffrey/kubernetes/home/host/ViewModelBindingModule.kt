package me.tevinjeffrey.kubernetes.home.host

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import me.tevinjeffrey.kubernetes.base.di.ViewModelFactory
import me.tevinjeffrey.kubernetes.base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.tevinjeffrey.kubernetes.home.setup.cert.CertViewModel

@Module
abstract class ViewModelBindingModule {

  @Binds
  abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

  @Binds
  @IntoMap
  @ViewModelKey(CertViewModel::class)
  abstract fun homeViewModel(viewModel: CertViewModel): ViewModel
}
