package org.automatease.calendiary.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

/** Creates the Room database for Android. */
fun createDatabase(context: Context): DiaryDatabase {
    val dbFile = context.getDatabasePath("diary.db")
    return Room.databaseBuilder<DiaryDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
