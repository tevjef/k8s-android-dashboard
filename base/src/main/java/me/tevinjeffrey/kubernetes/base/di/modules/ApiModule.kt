package me.tevinjeffrey.kubernetes.base.di.modules

import android.app.Application
import com.prolificinteractive.patrons.StringPreference
import dagger.Module
import dagger.Provides
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import io.fabric8.kubernetes.client.KubernetesClient
import me.tevinjeffrey.kubernetes.api.ssl.HttpClientUtils
import me.tevinjeffrey.kubernetes.base.BuildConfig
import me.tevinjeffrey.kubernetes.base.di.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
class ApiModule(private val app: Application) {

  /*@Provides
  @PerApp
  fun provideKubernetesClient(
      @Kubernetes okHttpClient: OkHttpClient,
      clientUtils: HttpClientUtils,
      @OAuthToken oauthToken: StringPreference,
      @ClientCert clientCert: StringPreference,
      @ClientKey clientKey: StringPreference,
      @ClusterCACert clusterCACert: StringPreference,
      @MasterUrl masterUrl: StringPreference): KubernetesClient {
    val config = ConfigBuilder()
        .withOauthToken(oauthToken.get())
        .withClientCertData(clientCert.get())
        .withClientKeyData(clientKey.get())
        .withCaCertData(clusterCACert.get())
        .withKeyStorePassphrase("")
        .withMasterUrl(masterUrl.get())
        .build()

    return DefaultKubernetesClient(clientUtils.createHttpClient(okHttpClient.newBuilder(), config), config)
  }*/

  @Provides
  @PerApp
  @Kubernetes
  fun provideOkHttpClient(
      client: OkHttpClient): OkHttpClient {
    val clientBuilder = client.newBuilder()

    if (BuildConfig.DEBUG) {
      val httpLoggingInterceptor = HttpLoggingInterceptor()
      httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
      clientBuilder.addInterceptor(httpLoggingInterceptor)
    }
    return clientBuilder.build()
  }

}
