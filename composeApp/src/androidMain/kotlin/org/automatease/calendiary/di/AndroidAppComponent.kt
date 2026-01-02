package org.automatease.calendiary.di

import android.content.Context
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.automatease.calendiary.data.local.createDatabase

/**
 * Android-specific AppComponent that provides the Room database. Uses kotlin-inject for
 * compile-time DI.
 */
@Component
abstract class AndroidAppComponent(
    @get:Provides val context: Context,
) : AppComponent(database = createDatabase(context))
