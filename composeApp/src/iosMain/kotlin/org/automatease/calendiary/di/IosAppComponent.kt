package org.automatease.calendiary.di

import me.tatarka.inject.annotations.Component
import org.automatease.calendiary.data.local.createDatabase

/**
 * iOS-specific AppComponent that provides the Room database. Uses kotlin-inject for compile-time
 * DI.
 */
@Component abstract class IosAppComponent : AppComponent(database = createDatabase())
