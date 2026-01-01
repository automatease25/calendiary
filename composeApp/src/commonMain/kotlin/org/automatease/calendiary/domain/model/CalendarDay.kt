package org.automatease.calendiary.domain.model

import kotlinx.datetime.LocalDate

/**
 * Represents a single day cell in the calendar grid. Can be either an actual day of the month or a
 * padding day from adjacent months.
 */
sealed class CalendarDay {
    /**
     * An actual day within the current month being displayed.
     *
     * @param date The date this day represents
     * @param isToday Whether this day is today's date
     * @param hasEntry Whether this day has a diary entry
     */
    data class Day(
        val date: LocalDate,
        val isToday: Boolean = false,
        val hasEntry: Boolean = false
    ) : CalendarDay()

    /**
     * A padding day from the previous month (shown grayed out).
     *
     * @param date The date from the previous month
     */
    data class PreviousMonthDay(val date: LocalDate) : CalendarDay()

    /**
     * A padding day from the next month (shown grayed out).
     *
     * @param date The date from the next month
     */
    data class NextMonthDay(val date: LocalDate) : CalendarDay()

    /** An empty cell (used when no date should be shown). */
    data object Empty : CalendarDay()
}

/**
 * Represents a complete calendar month grid.
 *
 * @param year The year of this calendar month
 * @param month The month (1-12)
 * @param days The list of calendar days forming the grid (always a multiple of 7)
 */
data class CalendarMonth(val year: Int, val month: Int, val days: List<CalendarDay>) {
    /** Returns the days organized as weeks (rows of 7 days). */
    fun asWeeks(): List<List<CalendarDay>> {
        return days.chunked(7)
    }
}
