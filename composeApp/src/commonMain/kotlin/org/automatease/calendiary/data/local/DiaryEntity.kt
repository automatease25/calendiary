package org.automatease.calendiary.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import org.automatease.calendiary.domain.model.DiaryEntry

/**
 * Room entity for storing diary entries in the database.
 * Uses the date string as the primary key to ensure one entry per day.
 */
@Entity(tableName = "diary_entries")
data class DiaryEntity(
    @PrimaryKey
    val dateString: String,
    val content: String
) {
    /**
     * Converts this entity to a domain model.
     */
    fun toDomain(): DiaryEntry {
        return DiaryEntry(
            date = LocalDate.parse(dateString),
            content = content
        )
    }

    companion object {
        /**
         * Creates an entity from a domain model.
         */
        fun fromDomain(entry: DiaryEntry): DiaryEntity {
            return DiaryEntity(
                dateString = entry.date.toString(),
                content = entry.content
            )
        }
    }
}
