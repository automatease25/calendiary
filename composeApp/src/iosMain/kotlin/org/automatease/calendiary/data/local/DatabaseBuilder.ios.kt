package org.automatease.calendiary.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * Returns the iOS-specific Room database builder.
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<DiaryDatabase> {
    val dbFilePath = documentDirectory() + "/${DiaryDatabase.DATABASE_NAME}"
    return Room.databaseBuilder<DiaryDatabase>(
        name = dbFilePath
    )
}

/**
 * Gets the iOS documents directory path.
 */
@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(documentDirectory?.path) { "Could not get document directory" }
}
