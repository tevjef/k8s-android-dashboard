package me.tevinjeffrey.kubernetes.base.di

import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME) annotation class Kubernetes

@Qualifier
@Retention(RUNTIME) annotation class OAuth

@Qualifier
@Retention(RUNTIME) annotation class ClientCert

@Qualifier
@Retention(RUNTIME) annotation class ClientKey

@Qualifier
@Retention(RUNTIME) annotation class ClusterCACert

@Qualifier
@Retention(RUNTIME) annotation class AllowInsecure

@Qualifier
@Retention(RUNTIME) annotation class MasterUrl

@Qualifier
@Retention(RUNTIME) annotation class ProxyUrl

@Qualifier
@Retention(RUNTIME) annotation class OAuthToken

@Qualifier
@Retention(RUNTIME) annotation class ShouldProxy

@Scope
@Retention(RUNTIME)
annotation class PerApp

@Scope
@Retention(RUNTIME)
annotation class PerActivity

@Scope
@Retention(RUNTIME)
annotation class PerFragment
