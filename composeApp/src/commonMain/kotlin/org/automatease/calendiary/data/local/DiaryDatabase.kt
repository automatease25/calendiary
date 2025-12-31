package org.automatease.calendiary.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

/**
 * Room database for the diary application.
 * Contains the diary_entries table.
 */
@Database(
    entities = [DiaryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
@ConstructedBy(DiaryDatabaseConstructor::class)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao

    companion object {
        const val DATABASE_NAME = "diary.db"
    }
}

/**
 * Room database constructor for KMP.
 * This is used by Room's KSP processor to generate the actual implementation.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DiaryDatabaseConstructor : RoomDatabaseConstructor<DiaryDatabase> {
    override fun initialize(): DiaryDatabase
}
