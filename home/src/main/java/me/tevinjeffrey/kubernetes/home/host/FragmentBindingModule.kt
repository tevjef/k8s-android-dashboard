package me.tevinjeffrey.kubernetes.home.host

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.tevinjeffrey.kubernetes.base.di.PerFragment
import me.tevinjeffrey.kubernetes.home.setup.cert.CertFragment

@Module
abstract class FragmentBindingModule {
  @PerFragment
  @ContributesAndroidInjector
  abstract fun contributesCertFragment(): CertFragment
}
