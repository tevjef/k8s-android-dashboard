package me.tevinjeffrey.kubernetes.home.host

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_host.*
import me.tevinjeffrey.kubernetes.base.Router
import me.tevinjeffrey.kubernetes.base.support.BaseActivity
import me.tevinjeffrey.kubernetes.home.R
import javax.inject.Inject

class HostActivity : BaseActivity(), HasSupportFragmentInjector {

  @Inject lateinit var router: Router
  @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

  private val navController: NavController by lazy { findNavController(R.id.nav_host_fragment) }

  override fun layoutId() = R.layout.activity_host

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    bottomNavigation.setOnNavigationItemSelectedListener { item ->
      onNavDestinationSelected(item, navController, true)
    }
    navController.addOnNavigatedListener({ _, destination ->
      val destinationId = destination.id
      val menu = bottomNavigation.menu
      var h = 0
      val size = menu.size()
      while (h < size) {
        val item = menu.getItem(h)
        if (item.itemId == destinationId) {
          item.isChecked = true
        }
        h++
      }
    })
  }

  override fun setupDependencyInjection() {
    HostActivityComponent.setupDependencyInjection(this)
  }

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

  override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

  companion object {
    internal fun onNavDestinationSelected(item: MenuItem,
                                          navController: NavController, popUp: Boolean): Boolean {
      val builder = NavOptions.Builder()
          .setLaunchSingleTop(true)
          .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
          .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
          .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
          .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
      if (popUp) {
        builder.setPopUpTo(navController.graph.startDestination, false)
      }
      val options = builder.build()
      return try {
        //TODO provide proper API instead of using Exceptions as Control-Flow.
        navController.navigate(item.itemId, null, options)
        true
      } catch (e: IllegalArgumentException) {
        false
      }

    }
  }
}
