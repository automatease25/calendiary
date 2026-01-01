package org.automatease.calendiary.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import org.automatease.calendiary.domain.model.DiaryEntry

/**
 * Repository interface for diary entries. Defines the contract for data operations on diary
 * entries.
 */
interface DiaryRepository {
    /**
     * Gets a diary entry for a specific date.
     *
     * @param date The date to get the entry for
     * @return Flow emitting the entry or null if not found
     */
    fun getEntry(date: LocalDate): Flow<DiaryEntry?>

    /**
     * Saves or updates a diary entry.
     *
     * @param entry The entry to save
     */
    suspend fun saveEntry(entry: DiaryEntry)

    /**
     * Gets all diary entries for a specific month.
     *
     * @param year The year
     * @param month The month (1-12)
     * @return Flow emitting list of entries for that month
     */
    fun getEntriesForMonth(year: Int, month: Int): Flow<List<DiaryEntry>>

    /**
     * Deletes a diary entry for a specific date.
     *
     * @param date The date of the entry to delete
     */
    suspend fun deleteEntry(date: LocalDate)

    /**
     * Gets all diary entries.
     *
     * @return Flow emitting all entries
     */
    fun getAllEntries(): Flow<List<DiaryEntry>>
}
