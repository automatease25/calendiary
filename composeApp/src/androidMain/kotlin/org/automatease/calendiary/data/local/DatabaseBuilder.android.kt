package org.automatease.calendiary.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Android-specific database builder implementation.
 */
private lateinit var applicationContext: Context

/**
 * Initializes the database context. Must be called from Application or Activity onCreate.
 */
fun initializeDatabaseContext(context: Context) {
    applicationContext = context.applicationContext
}

/**
 * Returns the Android-specific Room database builder.
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<DiaryDatabase> {
    val dbFile = applicationContext.getDatabasePath(DiaryDatabase.DATABASE_NAME)
    return Room.databaseBuilder<DiaryDatabase>(
        context = applicationContext,
        name = dbFile.absolutePath
    )
}
