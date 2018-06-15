package me.tevinjeffrey.kubernetes.home.host

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import me.tevinjeffrey.kubernetes.base.support.BaseActivity
import me.tevinjeffrey.kubernetes.base.Router
import me.tevinjeffrey.kubernetes.home.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class HostActivity : BaseActivity(), HasSupportFragmentInjector {

  @Inject lateinit var router: Router
  @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

  override fun layoutId() = R.layout.activity_host

  override fun setupDependencyInjection() { HostActivityComponent.setupDependencyInjection(this) }

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

  override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()
}
