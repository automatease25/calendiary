package org.automatease.calendiary.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room entity representing a diary entry in the database. */
@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey val date: String, // Stored as ISO format: "2024-01-15"
    val content: String,
)
