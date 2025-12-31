package org.automatease.calendiary.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

/**
 * Type converters for Room to handle kotlinx-datetime LocalDate.
 */
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
}
