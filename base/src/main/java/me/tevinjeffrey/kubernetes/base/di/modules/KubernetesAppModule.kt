package me.tevinjeffrey.kubernetes.base.di.modules

import android.app.Application
import android.content.Context
import android.view.inputmethod.InputMethodManager
import me.tevinjeffrey.kubernetes.base.di.PerApp
import dagger.Module
import dagger.Provides

@Module
class KubernetesAppModule(val app: Application) {

  @Provides
  @PerApp
  fun provideInputMethodService(): InputMethodManager {
    return app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  }
}
