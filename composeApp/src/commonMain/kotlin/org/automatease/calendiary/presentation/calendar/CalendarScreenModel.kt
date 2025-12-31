package org.automatease.calendiary.presentation.calendar

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.automatease.calendiary.domain.logic.CalendarLogic
import org.automatease.calendiary.domain.repository.DiaryRepository

/**
 * ScreenModel for the calendar screen.
 * Manages calendar state and handles user interactions.
 */
class CalendarScreenModel(
    private val diaryRepository: DiaryRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendarData()
    }

    /**
     * Creates the initial UI state with the current month.
     */
    private fun createInitialState(): CalendarUiState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return CalendarUiState(
            currentYear = today.year,
            currentMonth = today.month,
            dayHeaders = CalendarLogic.getDayHeaders(),
            monthDisplayName = CalendarLogic.getMonthDisplayName(today.month)
        )
    }

    /**
     * Handles UI events from the calendar screen.
     */
    fun onEvent(event: CalendarUiEvent) {
        when (event) {
            is CalendarUiEvent.NavigateToPreviousMonth -> navigateToPreviousMonth()
            is CalendarUiEvent.NavigateToNextMonth -> navigateToNextMonth()
            is CalendarUiEvent.NavigateToToday -> navigateToToday()
            is CalendarUiEvent.SelectDate -> { /* Navigation handled by screen */ }
        }
    }

    /**
     * Navigates to the previous month.
     */
    private fun navigateToPreviousMonth() {
        val (newYear, newMonth) = CalendarLogic.getPreviousMonth(
            _uiState.value.currentYear,
            _uiState.value.currentMonth
        )
        updateMonth(newYear, newMonth)
    }

    /**
     * Navigates to the next month.
     */
    private fun navigateToNextMonth() {
        val (newYear, newMonth) = CalendarLogic.getNextMonth(
            _uiState.value.currentYear,
            _uiState.value.currentMonth
        )
        updateMonth(newYear, newMonth)
    }

    /**
     * Navigates to the current month (today).
     */
    private fun navigateToToday() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        updateMonth(today.year, today.month)
    }

    /**
     * Updates the displayed month and reloads data.
     */
    private fun updateMonth(year: Int, month: Month) {
        _uiState.update { currentState ->
            currentState.copy(
                currentYear = year,
                currentMonth = month,
                monthDisplayName = CalendarLogic.getMonthDisplayName(month),
                isLoading = true
            )
        }
        loadCalendarData()
    }

    /**
     * Loads calendar data for the current month.
     * Combines the calendar grid with diary entries.
     */
    private fun loadCalendarData() {
        screenModelScope.launch {
            val year = _uiState.value.currentYear
            val month = _uiState.value.currentMonth

            diaryRepository.getEntriesForMonth(year, month.ordinal + 1)
                .collect { entries ->
                    val datesWithEntries = entries.map { it.date }.toSet()
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

                    val calendarMonth = CalendarLogic.generateCalendarGrid(
                        year = year,
                        month = month,
                        today = today,
                        datesWithEntries = datesWithEntries
                    )

                    _uiState.update { currentState ->
                        currentState.copy(
                            calendarMonth = calendarMonth,
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Refreshes calendar data (useful after returning from editor).
     */
    fun refresh() {
        loadCalendarData()
    }
}
