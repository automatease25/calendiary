package org.automatease.calendiary.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.automatease.calendiary.data.local.DiaryDatabase
import org.automatease.calendiary.data.local.getDatabaseBuilder
import org.automatease.calendiary.data.repository.SqlDiaryRepository
import org.automatease.calendiary.domain.repository.DiaryRepository
import org.automatease.calendiary.presentation.calendar.CalendarScreenModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Provides the Room database instance.
 * Uses BundledSQLiteDriver for KMP compatibility.
 */
fun provideDatabase(): DiaryDatabase {
    return getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

/**
 * Main application Koin module containing all dependencies.
 */
val appModule: Module = module {
    // Database
    single<DiaryDatabase> { provideDatabase() }
    
    // DAO
    single { get<DiaryDatabase>().diaryDao() }
    
    // Repository
    single<DiaryRepository> { SqlDiaryRepository(get()) }
    
    // ScreenModels
    factory { CalendarScreenModel(get()) }
}

/**
 * Returns all Koin modules for the application.
 */
fun getKoinModules(): List<Module> = listOf(appModule)
