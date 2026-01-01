package org.automatease.calendiary.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import org.automatease.calendiary.data.local.DiaryDao
import org.automatease.calendiary.data.local.DiaryEntity
import org.automatease.calendiary.domain.model.DiaryEntry
import org.automatease.calendiary.domain.repository.DiaryRepository

/**
 * SQL-based implementation of DiaryRepository using Room. Handles mapping between domain models and
 * database entities.
 */
class SqlDiaryRepository(private val diaryDao: DiaryDao) : DiaryRepository {

    override fun getEntry(date: LocalDate): Flow<DiaryEntry?> {
        return diaryDao.getEntry(date.toString()).map { entity -> entity?.toDomain() }
    }

    override suspend fun saveEntry(entry: DiaryEntry) {
        val entity = DiaryEntity.fromDomain(entry)
        diaryDao.insertOrUpdate(entity)
    }

    override fun getEntriesForMonth(year: Int, month: Int): Flow<List<DiaryEntry>> {
        val yearMonth = buildYearMonthString(year, month)
        return diaryDao.getEntriesForMonth(yearMonth).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteEntry(date: LocalDate) {
        diaryDao.deleteEntry(date.toString())
    }

    override fun getAllEntries(): Flow<List<DiaryEntry>> {
        return diaryDao.getAllEntries().map { entities -> entities.map { it.toDomain() } }
    }

    /** Builds a year-month string in format "yyyy-MM" for SQL LIKE queries. */
    private fun buildYearMonthString(year: Int, month: Int): String {
        val monthPadded = month.toString().padStart(2, '0')
        return "$year-$monthPadded"
    }
}
