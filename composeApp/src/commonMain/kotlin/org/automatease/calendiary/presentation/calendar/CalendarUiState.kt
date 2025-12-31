package org.automatease.calendiary.presentation.calendar

import kotlinx.datetime.Month
import org.automatease.calendiary.domain.model.CalendarMonth

/**
 * UI state for the calendar screen.
 * Follows UDF (Unidirectional Data Flow) pattern.
 */
data class CalendarUiState(
    val currentYear: Int,
    val currentMonth: Month,
    val calendarMonth: CalendarMonth? = null,
    val isLoading: Boolean = true,
    val monthDisplayName: String = "",
    val dayHeaders: List<String> = emptyList()
)

/**
 * Events that can be triggered from the calendar UI.
 */
sealed class CalendarUiEvent {
    data object NavigateToPreviousMonth : CalendarUiEvent()
    data object NavigateToNextMonth : CalendarUiEvent()
    data object NavigateToToday : CalendarUiEvent()
    data class SelectDate(val year: Int, val month: Int, val day: Int) : CalendarUiEvent()
}
