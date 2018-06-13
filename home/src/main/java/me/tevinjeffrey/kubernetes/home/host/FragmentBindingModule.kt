package me.tevinjeffrey.kubernetes.home.host

import me.tevinjeffrey.kubernetes.base.di.PerFragment
import me.tevinjeffrey.kubernetes.home.HomeFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBindingModule {
  @PerFragment
  @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
  abstract fun contributesHomeFragment(): HomeFragment
}

@Module
abstract class HomeFragmentModule { }
