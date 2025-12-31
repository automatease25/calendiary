package org.automatease.calendiary.data.local

import androidx.room.RoomDatabase

/**
 * Expect function to get a platform-specific Room database builder.
 * Each platform (Android, iOS) provides its own actual implementation.
 */
expect fun getDatabaseBuilder(): RoomDatabase.Builder<DiaryDatabase>
