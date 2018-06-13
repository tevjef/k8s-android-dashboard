package me.tevinjeffrey.kubernetes.base.di

import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME) annotation class Kubernetes

@Qualifier
@Retention(RUNTIME) annotation class OAuth

@Qualifier
@Retention(RUNTIME) annotation class AccessToken

@Qualifier
@Retention(RUNTIME) annotation class RefreshToken

@Scope
@Retention(RUNTIME)
annotation class PerApp

@Scope
@Retention(RUNTIME)
annotation class PerActivity

@Scope
@Retention(RUNTIME)
annotation class PerFragment
