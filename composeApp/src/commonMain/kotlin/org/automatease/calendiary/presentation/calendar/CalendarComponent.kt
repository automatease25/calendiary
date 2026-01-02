package org.automatease.calendiary.presentation.calendar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlin.time.Clock
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.automatease.calendiary.domain.logic.CalendarLogic
import org.automatease.calendiary.domain.repository.DiaryRepository

/** Interface for the calendar component. Uses Decompose Value for state. */
interface CalendarComponent {
    val state: Value<CalendarUiState>

    fun onEvent(event: CalendarUiEvent)

    fun refresh()

    fun onDayClick(date: LocalDate)
}

/**
 * Default implementation of CalendarComponent using Decompose. Uses Decompose Value for state
 * management. Factories are manually created in AppComponent for assisted injection pattern.
 */
class DefaultCalendarComponent(
    componentContext: ComponentContext,
    private val onNavigateToEditor: (LocalDate) -> Unit,
    private val diaryRepository: DiaryRepository,
) : CalendarComponent, ComponentContext by componentContext {

    private val scope = coroutineScope()

    private val _state = MutableValue(createInitialState())
    override val state: Value<CalendarUiState> = _state

    init {
        loadCalendarData()
    }

    /** Gets today's date in the current system timezone. */
    private fun today(): LocalDate {
        val now = Clock.System.now()
        val instant = Instant.fromEpochMilliseconds(now.toEpochMilliseconds())
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    /** Creates the initial UI state with the current month. */
    private fun createInitialState(): CalendarUiState {
        val todayDate = today()
        return CalendarUiState(
            currentYear = todayDate.year,
            currentMonth = todayDate.month,
            dayHeaders = CalendarLogic.getDayHeaders(),
            monthDisplayName = CalendarLogic.getMonthDisplayName(todayDate.month),
        )
    }

    /** Handles UI events from the calendar screen. */
    override fun onEvent(event: CalendarUiEvent) {
        when (event) {
            is CalendarUiEvent.NavigateToPreviousMonth -> navigateToPreviousMonth()
            is CalendarUiEvent.NavigateToNextMonth -> navigateToNextMonth()
            is CalendarUiEvent.NavigateToToday -> navigateToToday()
            is CalendarUiEvent.SelectDate -> {
                /* Navigation handled by onDayClick */
            }
        }
    }

    override fun onDayClick(date: LocalDate) {
        onNavigateToEditor(date)
    }

    /** Navigates to the previous month. */
    private fun navigateToPreviousMonth() {
        val currentState = _state.value
        val (newYear, newMonth) =
            CalendarLogic.getPreviousMonth(currentState.currentYear, currentState.currentMonth)
        updateMonth(newYear, newMonth)
    }

    /** Navigates to the next month. */
    private fun navigateToNextMonth() {
        val currentState = _state.value
        val (newYear, newMonth) =
            CalendarLogic.getNextMonth(currentState.currentYear, currentState.currentMonth)
        updateMonth(newYear, newMonth)
    }

    /** Navigates to the current month (today). */
    private fun navigateToToday() {
        val todayDate = today()
        updateMonth(todayDate.year, todayDate.month)
    }

    /** Updates the displayed month and reloads data. */
    private fun updateMonth(year: Int, month: Month) {
        _state.update { currentState ->
            currentState.copy(
                currentYear = year,
                currentMonth = month,
                monthDisplayName = CalendarLogic.getMonthDisplayName(month),
                isLoading = true,
            )
        }
        loadCalendarData()
    }

    /** Loads calendar data for the current month. Combines the calendar grid with diary entries. */
    private fun loadCalendarData() {
        scope.launch {
            val currentState = _state.value
            val year = currentState.currentYear
            val month = currentState.currentMonth

            diaryRepository.getEntriesForMonth(year, month.ordinal + 1).collect { entries ->
                val datesWithEntries = entries.map { it.date }.toSet()
                val todayDate = today()

                val calendarMonth =
                    CalendarLogic.generateCalendarGrid(
                        year = year,
                        month = month,
                        today = todayDate,
                        datesWithEntries = datesWithEntries,
                    )

                _state.update { it.copy(calendarMonth = calendarMonth, isLoading = false) }
            }
        }
    }

    /** Refreshes calendar data (useful after returning from editor). */
    override fun refresh() {
        loadCalendarData()
    }
}
