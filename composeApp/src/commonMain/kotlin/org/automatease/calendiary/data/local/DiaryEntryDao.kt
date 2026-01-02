package org.automatease.calendiary.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Data Access Object for diary entries. */
@Dao
interface DiaryEntryDao {
    /** Gets a single diary entry by date. */
    @Query("SELECT * FROM diary_entries WHERE date = :date")
    suspend fun getEntryByDate(date: String): DiaryEntryEntity?

    /** Gets all entries for a specific month (date pattern: YYYY-MM-%). */
    @Query("SELECT * FROM diary_entries WHERE date LIKE :monthPattern ORDER BY date ASC")
    fun getEntriesForMonth(monthPattern: String): Flow<List<DiaryEntryEntity>>

    /** Gets all diary entries. */
    @Query("SELECT * FROM diary_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<DiaryEntryEntity>>

    /** Inserts or updates a diary entry. */
    @Upsert suspend fun upsertEntry(entry: DiaryEntryEntity)

    /** Deletes a diary entry by date. */
    @Query("DELETE FROM diary_entries WHERE date = :date")
    suspend fun deleteEntryByDate(date: String)

    /** Checks if an entry exists for a specific date. */
    @Query("SELECT EXISTS(SELECT 1 FROM diary_entries WHERE date = :date)")
    suspend fun hasEntryForDate(date: String): Boolean
}
