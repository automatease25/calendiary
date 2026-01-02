package org.automatease.calendiary.domain.error

import kotlinx.datetime.LocalDate

/**
 * Sealed interface hierarchy for all domain errors. Using sealed interfaces enables exhaustive
 * when-expressions and compile-time verification of error handling.
 */
sealed interface DomainError {
    /** Human-readable message for logging/debugging. */
    val message: String
}

/** Errors related to diary entry operations. */
sealed interface DiaryError : DomainError {
    /** Entry was not found for the specified date. */
    data class NotFound(val date: LocalDate) : DiaryError {
        override val message: String = "Diary entry not found for date: $date"
    }

    /** Failed to persist entry to storage. */
    data class PersistenceFailure(val cause: String) : DiaryError {
        override val message: String = "Failed to persist diary entry: $cause"
    }

    /** Entry content validation failed. */
    data class ValidationError(val reason: String) : DiaryError {
        override val message: String = "Validation failed: $reason"
    }
}

/** Errors related to database operations. */
sealed interface DatabaseError : DomainError {
    /** Database connection or driver initialization failed. */
    data class ConnectionFailure(val cause: String) : DatabaseError {
        override val message: String = "Database connection failed: $cause"
    }

    /** Query execution failed. */
    data class QueryFailure(val cause: String) : DatabaseError {
        override val message: String = "Query execution failed: $cause"
    }
}
