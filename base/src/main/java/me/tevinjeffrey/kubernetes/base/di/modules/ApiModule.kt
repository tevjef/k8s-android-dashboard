package me.tevinjeffrey.kubernetes.base.di.modules

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import me.tevinjeffrey.kubernetes.api.KubernetesApi
import me.tevinjeffrey.kubernetes.api.KubernetesApiClient
import me.tevinjeffrey.kubernetes.api.KubernetesAuthInterceptor
import me.tevinjeffrey.kubernetes.api.KubernetesAuthenticator
import me.tevinjeffrey.kubernetes.api.RxSchedulers
import me.tevinjeffrey.kubernetes.base.BuildConfig
import me.tevinjeffrey.kubernetes.base.di.AccessToken
import me.tevinjeffrey.kubernetes.base.di.Kubernetes
import me.tevinjeffrey.kubernetes.base.di.PerApp
import me.tevinjeffrey.kubernetes.base.di.RefreshToken
import com.prolificinteractive.patrons.StringPreference
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import io.fabric8.kubernetes.client.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@Module
class ApiModule(private val app: Application) {

  @Provides
  @PerApp
  @Kubernetes
  fun provideApiEndpointPreference(preferences: SharedPreferences): StringPreference {
    return StringPreference(preferences, "api_endpoint", URL)
  }

  @Provides
  @PerApp
  fun provideAuthInterceptor(@AccessToken accessToken: StringPreference): KubernetesAuthInterceptor {
    return KubernetesAuthInterceptor(accessToken)
  }

  @Provides
  @PerApp
  fun provideKubernetesClient(): KubernetesClient {
    return DefaultKubernetesClient(ConfigBuilder()
        .withOauthToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJkYXNoYm9hcmQtdG9rZW4tMm1xdmoiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGFzaGJvYXJkIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiNDgzZGI2NzQtOGFhYi0xMWU3LWIzYjYtNDIwMTBhOGUwZmRkIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50Omt1YmUtc3lzdGVtOmRhc2hib2FyZCJ9.N8Oq_m5Z8m3T1IunlrSEu3LMaqkjq86pbPPwTqe9eNFp479RzFIZaVtGV0cxQW7n5cSdXeZzEYJujmD6FrHjyM2Ww54kWh1_e256sJbxMlktGO6dDfBE92Vdp7P2QyY8At3-QZ2fJdt8eIwj0Z71VaiWfkrza-T5nsE5iPHPn9_4tJGAOXY0KNEeDF__NVkFJQtNusGPyPrXscBbRTYcfo7sf-DZulUn1mHl4NKx1ibuGE76mkI4sFFxCKZWHSzOVXvXTO5ymT05DNuVzgp4REPLL6Z4yhSmTpP7VsVVvoDaUlXSHsHYUWy0uaxiwiM3TNTOC4D5PQ4oYjImgJJPRg")
        .withClientCertData("-----BEGIN CERTIFICATE-----MIIC8jCCAdqgAwIBAgIIJH5MMwIZhWQwDQYJKoZIhvcNAQELBQAwFTETMBEGA1UEAxMKa3ViZXJuZXRlczAeFw0xODA0MzAwNDQ4NTJaFw0xOTA0MzAwNDUwMTZaMDQxFzAVBgNVBAoTDnN5c3RlbTptYXN0ZXJzMRkwFwYDVQQDExBrdWJlcm5ldGVzLWFkbWluMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwA2yumMQmXtj8PpV77eshmrSdaAPUG0NQohw10WyTVudCz5G8ltrxFYRjkgWvv+7U+nqxrEq2MjahtdJwuJSIhvOmTJigcC/wOQoMcFIoE4urQbE6q7tHqxuBPsTUdVYPY4n/HGU8ZNbAKBkX0C1Fo4VQ8YCatHOjOlYc9xWp4gbS/K9dvrJHriKhBklp9ZoEkmBEcMN9zbuwPmPpFwAylCs6LIgYtIhAcTCrvNMlNx0/mxkaDP5TIUxPU0ae21jviKP1UdCGBXiUJCdb5qmi0zNDHO+Dh1FfYmkQRAAvtghBgf98ssQtWE7OjyM8qUWsMnCS5j10DPdPlyO\n" +
            "V93SqwIDAQABoycwJTAOBgNVHQ8BAf8EBAMCBaAwEwYDVR0lBAwwCgYIKwYBBQUH\n" +
            "AwIwDQYJKoZIhvcNAQELBQADggEBAEv3h1WrmiiNFmiklVHafiDieVmtcl8UMR10\n" +
            "b0HsySIXGKGr8LiHcceMPfjbLhcl8i+ynboKGWvnrkF+MD0mvNgqInE4XXu4E42E\n" +
            "KkCmpnKG78ffqIne+18l8VTPHxGj+Qfm4Nqzo/PA3FIbm/J1wyO7LjG/Q0tLOMAW\n" +
            "F75wAnVvFRR5beFVPdfMosNC4DAUUKM4i0ihlozqvpA0Mth9lF+mLBrzE1R/f6ef\n" +
            "z4u6C+U8iP2uRCa928yZGScKjcAgqzv8QSCGRf0/03DnLrtx/2hIwTSRsWDBDqb+\n" +
            "gAwwDw+lekG2ZBYoJla2JnAECh6R44IDta7P9nZdYifgoPNXpXs=\n" +
            "-----END CERTIFICATE----")
        .withClientKeyData("-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpQIBAAKCAQEAwA2yumMQmXtj8PpV77eshmrSdaAPUG0NQohw10WyTVudCz5G\n" +
            "8ltrxFYRjkgWvv+7U+nqxrEq2MjahtdJwuJSIhvOmTJigcC/wOQoMcFIoE4urQbE\n" +
            "6q7tHqxuBPsTUdVYPY4n/HGU8ZNbAKBkX0C1Fo4VQ8YCatHOjOlYc9xWp4gbS/K9\n" +
            "dvrJHriKhBklp9ZoEkmBEcMN9zbuwPmPpFwAylCs6LIgYtIhAcTCrvNMlNx0/mxk\n" +
            "aDP5TIUxPU0ae21jviKP1UdCGBXiUJCdb5qmi0zNDHO+Dh1FfYmkQRAAvtghBgf9\n" +
            "8ssQtWE7OjyM8qUWsMnCS5j10DPdPlyOV93SqwIDAQABAoIBAQCDlJ8ATkEgNsbJ\n" +
            "YmogXAIK8gMRbcEwRBbSn7JX+ztm/r6A5oErKPGTybgreD6FS6annlspGFcXwbze\n" +
            "p3+00uSc09pfYYLWDR4TZP4Y4KkGk6otB7dAZuq0mkSiiva4mWfkfJHuxk7amC/A\n" +
            "JSO/kxC+zEfLcE1VrDtMlc7xSmPgURJj+gSO/FTdkmxwiJtZqB86W0SG8ziR7NsR\n" +
            "mqgpl9S8LOd6aoM+yZx0MW7NK/TEa4emmk17ewi2jQ9ca5/rsW3BFIqiKak6eKWW\n" +
            "Ug7gcqgSpKIKgTqFN5u5F4MFr0marIVehxx4tQleWC6XipIOGkMQZNBZTgP3VKvA\n" +
            "hnxObhxBAoGBAO6YK9FCDvErRZVHULkvqJLjgNvcuHOgeoKQjCodHYhakUGmtAaw\n" +
            "VFY4SbrBeARIbdYbIU1oCZ2q1e4/wGpHp9IW/naJnbJldm6QNr0lp/cofrqcL0RC\n" +
            "x1niaEsew7wfctH01S5Hg1ocB1pjN+h+9Exmcio5pN5Lk9Zms9tFPJMhAoGBAM4Q\n" +
            "XDqOR8qZSPMiQ+lkW7ClLr3WDSGglBuaPeTCkUU+SjSImtiYuMK7ZhEb9gXv/MYo\n" +
            "mDCYee0PdKDvljsE1egeL2r0AcEpoSr+3oqn+onoouiFVS5JpPiViqFTCxxGGWTf\n" +
            "+DY+m6HZbpp1EoK0/jQK4svDq7Xst90rZneIf7hLAoGAcq7IGPxEjF19Q8SUo36p\n" +
            "63jY/lU4f7N6T3nFNZTrFhhaS5EiZTiqQQsrPU4zLvPVf51ow3knbhaoX61bAnrQ\n" +
            "yqXsx/Nbzl0AsSI2fXI1KxLW30GlYo0qnyS5dqoh8TG4kfy531XWaCCi1CpuvB0R\n" +
            "tgtkArHUclknhV22Q8Yi2KECgYEAvdYkoW1ihishIZx3pBRoxBvpK8p+3nPo/mTi\n" +
            "kFUPMBNqmWsf/Fr2G7A5H5n61Q16ebP8QQi3isWqVHlklYqm5eipFock9qc7azjS\n" +
            "HDsUfOhs7HmpN7Nw3/IkfOrcWXfa/Su4p4CWMEz3sCQaoX/MkHVW6xGgp0ElQgQx\n" +
            "qf1+bHECgYEA4ebsDoiJusRuuaySahBqeRcInqASc7P87yCpXBIubi/0Gun2Ymoe\n" +
            "zIvV/YJNhgv8h6E1ejC6jtPOQ3heuj8OkzDuucLftvRNRCyxEte0vLamoKLUndoo\n" +
            "QvLbil8z1sdRKc6/GavqKF6yavWda9JqBeIpEjvxtMddgSgPEMc8ovw=\n" +
            "-----END RSA PRIVATE KEY----")
        .withCaCertData("-----BEGIN CERTIFICATE-----\n" +
            "MIIDDDCCAfSgAwIBAgIRAJqyvNATAlyWM7anklIb4G8wDQYJKoZIhvcNAQELBQAw\n" +
            "LzEtMCsGA1UEAxMkY2NmZmFkYzctNWQ0Mi00OTI4LWJlYmUtOTE0MTYzNmQwNjFh\n" +
            "MB4XDTE3MDgyNjIwMzkyMloXDTIyMDgyNTIxMzkyMlowLzEtMCsGA1UEAxMkY2Nm\n" +
            "ZmFkYzctNWQ0Mi00OTI4LWJlYmUtOTE0MTYzNmQwNjFhMIIBIjANBgkqhkiG9w0B\n" +
            "AQEFAAOCAQ8AMIIBCgKCAQEAvzXENin/VVqbGypoaHhK2FQeErmSRH/YV08ZioHd\n" +
            "mKGT5hCH7dNZbRNw1YmO7t0G+P3uklIAwr5nDf9U60OYHXbDGhynxhlWUkzv2ABr\n" +
            "RNcjUAbH+L/jS0s0nmfypSdUapnK1s8WF3rPVkj643VFEfXsJWCgj4Rwpkww+bVi\n" +
            "jIH96tVIP4zwL0Dj8Fle3H2yB5qHaKKx171khCMW4SWakIAzOXUQ2P1BM9K4npI6\n" +
            "bagYwR4SrHBFvdZzNjDxZ+AHZ+3XvUYTQH9DlNOBLeQZuDiEN0Aw8qmghxVlBrqD\n" +
            "DibLHKsMobs9NdJzyNW1Iux2gbTs9szYO8KiLfwjcjPZwwIDAQABoyMwITAOBgNV\n" +
            "HQ8BAf8EBAMCAgQwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEA\n" +
            "SMapcfHTS+Mb6rPFkCxRiibcNcH35aXDUrWwhuT2VYCC+tQlUtGo1U7WkkT8ogs5\n" +
            "8x3Ra9KsHvFMDORHG1jEwsGLzOB13ohdl0ITHk7n9Mh8xZmnKukMxY79WsHN9Uzw\n" +
            "ilTflNLpKGWtOSVWbyrn4CTHbJ2gq5iyNzN+QJ4Urk33a0bIi2IKlGy4XXCQZfmS\n" +
            "AtWcv/5hwLADua1sS/6iZnJ3/03e2HEuVMosHIOLLk6A4uqITvRs5czTlLbuMd6T\n" +
            "n/PFC2j6DA+PRrgG7EK9xAuw/L3/j+lN/R8waoW3sX5rbXiqDuxAmvVurWF9AafN\n" +
            "tebKcMVFr6b0MCGstsde1Q==\n" +
            "-----END CERTIFICATE-----")
        .withUsername("admin")
        .withPassword("X2O3wvhbU81AeZaA")
        .withMasterUrl("https://35.196.51.167")
        .build())
  }

  @Provides
  @PerApp
  fun provideAuthenticator(
      @AccessToken accessToken: StringPreference,
      @RefreshToken refreshToken: StringPreference,
      @Kubernetes url: HttpUrl,
      gson: Gson): KubernetesAuthenticator {
    return KubernetesAuthenticator(
        accessToken,
        refreshToken,
        url,
        gson
    )
  }

  @Provides
  @PerApp
  @Kubernetes
  fun provideHttpUrl() = HttpUrl.parse(URL)!!

  @Provides
  @PerApp
  @Kubernetes
  fun provideOkHttpClient(
      client: OkHttpClient,
      authInterceptor: KubernetesAuthInterceptor,
      authenticator: KubernetesAuthenticator,
      chuck: ChuckInterceptor): OkHttpClient {
    val clientBuilder = client.newBuilder()

    if (BuildConfig.DEBUG) {
      val httpLoggingInterceptor = HttpLoggingInterceptor()
      httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
      clientBuilder.addInterceptor(httpLoggingInterceptor)
      clientBuilder.addInterceptor(chuck)
    }
    clientBuilder.addNetworkInterceptor(authInterceptor)
    clientBuilder.authenticator(authenticator)
    return clientBuilder.build()
  }

  @Provides
  @PerApp
  @Kubernetes
  fun provideRetrofit(
      gson: Gson,
      @Kubernetes baseUrl: HttpUrl,
      @Kubernetes client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
  }

  @Provides
  @PerApp
  fun provideChuck(): ChuckInterceptor = ChuckInterceptor(app)

  @Provides
  @PerApp
  fun provideBootstrapApiClient(
      @Kubernetes retrofit: Retrofit,
      scheduler: RxSchedulers,
      @AccessToken accessToken: StringPreference,
      @RefreshToken refreshToken: StringPreference): KubernetesApi {
    return KubernetesApiClient(
        retrofit.create(KubernetesApi::class.java),
        scheduler
    )
  }

  companion object {
    private const val URL: String = "http://api.pandroid.com/v1/"
  }
}
