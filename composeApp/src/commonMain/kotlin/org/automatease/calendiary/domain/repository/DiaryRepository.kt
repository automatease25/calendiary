package org.automatease.calendiary.domain.repository

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import org.automatease.calendiary.domain.error.DiaryError
import org.automatease.calendiary.domain.model.DiaryEntry

/**
 * Repository interface for diary entries using typed error handling. All operations return Either
 * types for compile-time verified error handling. Never throws exceptions - uses Arrow Either for
 * typed errors.
 */
interface DiaryRepository {
    /**
     * Gets a diary entry for a specific date.
     *
     * @param date The date to get the entry for
     * @return Either containing DiaryError.NotFound or the entry
     */
    suspend fun getEntry(date: LocalDate): Either<DiaryError, DiaryEntry>

    /**
     * Saves or updates a diary entry.
     *
     * @param entry The entry to save
     * @return Either containing DiaryError or Unit on success
     */
    suspend fun saveEntry(entry: DiaryEntry): Either<DiaryError, Unit>

    /**
     * Gets all diary entries for a specific month as a reactive Flow. The Flow itself does not
     * error - errors are represented as Either within the emitted values.
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
     * @return Either containing DiaryError or Unit on success
     */
    suspend fun deleteEntry(date: LocalDate): Either<DiaryError, Unit>

    /**
     * Gets all diary entries as a reactive Flow.
     *
     * @return Flow emitting all entries
     */
    fun getAllEntries(): Flow<List<DiaryEntry>>

    /**
     * Checks if an entry exists for a specific date.
     *
     * @param date The date to check
     * @return Either containing DiaryError or Boolean indicating existence
     */
    suspend fun hasEntry(date: LocalDate): Either<DiaryError, Boolean>
}
