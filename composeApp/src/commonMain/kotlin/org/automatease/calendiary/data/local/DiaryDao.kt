package org.automatease.calendiary.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for diary entries.
 * Provides methods to interact with the diary_entries table.
 */
@Dao
interface DiaryDao {
    /**
     * Gets a diary entry for a specific date.
     * @param dateString The date in ISO format (yyyy-MM-dd)
     * @return Flow emitting the entry or null if not found
     */
    @Query("SELECT * FROM diary_entries WHERE dateString = :dateString")
    fun getEntry(dateString: String): Flow<DiaryEntity?>

    /**
     * Inserts a new entry or updates an existing one.
     * Uses UPSERT semantics (insert or replace).
     * @param entry The diary entity to insert or update
     */
    @Upsert
    suspend fun insertOrUpdate(entry: DiaryEntity)

    /**
     * Gets all diary entries for a specific month.
     * @param yearMonth The year-month prefix in format "yyyy-MM"
     * @return Flow emitting list of entries for that month
     */
    @Query("SELECT * FROM diary_entries WHERE dateString LIKE :yearMonth || '%'")
    fun getEntriesForMonth(yearMonth: String): Flow<List<DiaryEntity>>

    /**
     * Deletes a diary entry for a specific date.
     * @param dateString The date in ISO format (yyyy-MM-dd)
     */
    @Query("DELETE FROM diary_entries WHERE dateString = :dateString")
    suspend fun deleteEntry(dateString: String)

    /**
     * Gets all diary entries.
     * @return Flow emitting all entries
     */
    @Query("SELECT * FROM diary_entries ORDER BY dateString DESC")
    fun getAllEntries(): Flow<List<DiaryEntity>>
}
