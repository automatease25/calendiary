package org.automatease.calendiary.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

/** Creates the Room database for iOS. */
fun createDatabase(): DiaryDatabase {
    val dbFilePath = NSHomeDirectory() + "/Documents/diary.db"
    return Room.databaseBuilder<DiaryDatabase>(name = dbFilePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
