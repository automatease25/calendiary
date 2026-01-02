package org.automatease.calendiary.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.automatease.calendiary.data.local.DiaryDatabase
import org.automatease.calendiary.data.local.DiaryEntryEntity
import org.automatease.calendiary.domain.error.DiaryError
import org.automatease.calendiary.domain.model.DiaryEntry
import org.automatease.calendiary.domain.repository.DiaryRepository

/**
 * Room-based implementation of DiaryRepository. Uses Arrow Either for typed error handling and Room
 * for database operations.
 */
class RoomDiaryRepository(
    private val database: DiaryDatabase,
    private val ioDispatcher: CoroutineDispatcher,
) : DiaryRepository {

    private val dao
        get() = database.diaryEntryDao()

    override suspend fun getEntry(date: LocalDate): Either<DiaryError, DiaryEntry> =
        withContext(ioDispatcher) {
            runCatching { dao.getEntryByDate(date.toString()) }
                .fold(
                    onSuccess = { entity ->
                        entity?.toDomain()?.right() ?: DiaryError.NotFound(date).left()
                    },
                    onFailure = { throwable ->
                        DiaryError.PersistenceFailure(throwable.message ?: "Unknown database error")
                            .left()
                    },
                )
        }

    override suspend fun saveEntry(entry: DiaryEntry): Either<DiaryError, Unit> =
        withContext(ioDispatcher) {
            if (entry.content.isBlank()) {
                return@withContext DiaryError.ValidationError("Content cannot be blank").left()
            }

            runCatching { dao.upsertEntry(entry.toEntity()) }
                .fold(
                    onSuccess = { Unit.right() },
                    onFailure = { throwable ->
                        DiaryError.PersistenceFailure(throwable.message ?: "Failed to save entry")
                            .left()
                    },
                )
        }

    override fun getEntriesForMonth(year: Int, month: Int): Flow<List<DiaryEntry>> {
        val monthPattern = buildMonthPattern(year, month)
        return dao.getEntriesForMonth(monthPattern).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteEntry(date: LocalDate): Either<DiaryError, Unit> =
        withContext(ioDispatcher) {
            runCatching { dao.deleteEntryByDate(date.toString()) }
                .fold(
                    onSuccess = { Unit.right() },
                    onFailure = { throwable ->
                        DiaryError.PersistenceFailure(throwable.message ?: "Failed to delete entry")
                            .left()
                    },
                )
        }

    override fun getAllEntries(): Flow<List<DiaryEntry>> {
        return dao.getAllEntries().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun hasEntry(date: LocalDate): Either<DiaryError, Boolean> =
        withContext(ioDispatcher) {
            runCatching { dao.hasEntryForDate(date.toString()) }
                .fold(
                    onSuccess = { it.right() },
                    onFailure = { throwable ->
                        DiaryError.PersistenceFailure(
                                throwable.message ?: "Failed to check entry existence")
                            .left()
                    },
                )
        }

    /** Builds a pattern for SQL LIKE queries: "yyyy-MM-%". */
    private fun buildMonthPattern(year: Int, month: Int): String {
        val monthPadded = month.toString().padStart(2, '0')
        return "$year-$monthPadded-%"
    }
}

/** Converts Room entity to domain model. */
private fun DiaryEntryEntity.toDomain(): DiaryEntry {
    return DiaryEntry(date = LocalDate.parse(date), content = content)
}

/** Converts domain model to Room entity. */
private fun DiaryEntry.toEntity(): DiaryEntryEntity {
    return DiaryEntryEntity(date = date.toString(), content = content)
}
