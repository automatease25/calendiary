package org.automatease.calendiary.domain.model

import kotlinx.datetime.LocalDate

/**
 * Domain model representing a diary entry.
 * This is a pure data class without any database/framework annotations.
 */
data class DiaryEntry(
    val date: LocalDate,
    val content: String
)
