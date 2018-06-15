package me.tevinjeffrey.kubernetes.home.host

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.tevinjeffrey.kubernetes.base.di.PerFragment
import me.tevinjeffrey.kubernetes.home.settings.SettingsFragment

@Module
abstract class FragmentBindingModule {
  @PerFragment
  @ContributesAndroidInjector
  abstract fun contributesSettingsFragment(): SettingsFragment
}
