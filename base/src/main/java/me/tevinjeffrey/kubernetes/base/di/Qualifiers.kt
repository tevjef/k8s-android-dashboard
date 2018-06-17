package me.tevinjeffrey.kubernetes.base.di

import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME) annotation class Kubernetes

@Scope
@Retention(RUNTIME)
annotation class PerApp

@Scope
@Retention(RUNTIME)
annotation class PerActivity

@Scope
@Retention(RUNTIME)
annotation class PerFragment
