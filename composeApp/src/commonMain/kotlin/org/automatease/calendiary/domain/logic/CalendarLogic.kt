package org.automatease.calendiary.domain.logic

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.automatease.calendiary.domain.model.CalendarDay
import org.automatease.calendiary.domain.model.CalendarMonth

/** Logic for generating calendar grids and performing date calculations. */
object CalendarLogic {

    /**
     * Generates a calendar grid for the specified year and month. The grid is aligned to start on
     * Monday and includes padding days from the previous and next months to complete the grid.
     *
     * @param year The year
     * @param month The month
     * @param today Optional today's date for highlighting (defaults to current date)
     * @param datesWithEntries Set of dates that have diary entries
     * @return A CalendarMonth containing the complete grid
     */
    fun generateCalendarGrid(
        year: Int,
        month: Month,
        today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        datesWithEntries: Set<LocalDate> = emptySet(),
    ): CalendarMonth {
        val days = mutableListOf<CalendarDay>()

        // First day of the month
        val firstDayOfMonth = LocalDate(year, month, 1)

        // Last day of the month
        val lastDayOfMonth = getLastDayOfMonth(year, month)

        // Calculate padding days needed at the start (week starts on Monday)
        val startPaddingDays = calculateStartPadding(firstDayOfMonth.dayOfWeek)

        // Add padding days from previous month
        if (startPaddingDays > 0) {
            val previousMonthLastDay = firstDayOfMonth.minus(1, DateTimeUnit.DAY)
            for (i in (startPaddingDays - 1) downTo 0) {
                val paddingDate = previousMonthLastDay.minus(i, DateTimeUnit.DAY)
                days.add(CalendarDay.PreviousMonthDay(paddingDate))
            }
        }

        // Add all days of the current month
        var currentDate = firstDayOfMonth
        while (currentDate <= lastDayOfMonth) {
            days.add(
                CalendarDay.Day(
                    date = currentDate,
                    isToday = currentDate == today,
                    hasEntry = currentDate in datesWithEntries,
                ))
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }

        // Calculate padding days needed at the end to complete the grid
        val totalDaysSoFar = days.size
        val endPaddingDays = calculateEndPadding(totalDaysSoFar)

        // Add padding days from next month
        if (endPaddingDays > 0) {
            val nextMonthFirstDay = lastDayOfMonth.plus(1, DateTimeUnit.DAY)
            for (i in 0 until endPaddingDays) {
                val paddingDate = nextMonthFirstDay.plus(i, DateTimeUnit.DAY)
                days.add(CalendarDay.NextMonthDay(paddingDate))
            }
        }

        return CalendarMonth(year = year, month = month.ordinal + 1, days = days)
    }

    /**
     * Calculates the number of padding days needed at the start of the grid. Week starts on Monday
     * (index 0).
     *
     * @param firstDayOfWeek The day of week of the first day of the month
     * @return Number of padding days (0-6)
     */
    private fun calculateStartPadding(firstDayOfWeek: DayOfWeek): Int {
        // Monday = 0, Tuesday = 1, ..., Sunday = 6
        return when (firstDayOfWeek) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
        }
    }

    /**
     * Calculates the number of padding days needed at the end of the grid. The grid should be a
     * complete set of weeks (multiple of 7).
     *
     * @param totalDaysSoFar Current number of days in the grid
     * @return Number of padding days needed to complete the last week
     */
    private fun calculateEndPadding(totalDaysSoFar: Int): Int {
        val remainder = totalDaysSoFar % 7
        return if (remainder == 0) 0 else 7 - remainder
    }

    /**
     * Gets the last day of the specified month.
     *
     * @param year The year
     * @param month The month
     * @return The last date of the month
     */
    private fun getLastDayOfMonth(year: Int, month: Month): LocalDate {
        val daysInMonth =
            when (month) {
                Month.JANUARY -> 31
                Month.FEBRUARY -> if (isLeapYear(year)) 29 else 28
                Month.MARCH -> 31
                Month.APRIL -> 30
                Month.MAY -> 31
                Month.JUNE -> 30
                Month.JULY -> 31
                Month.AUGUST -> 31
                Month.SEPTEMBER -> 30
                Month.OCTOBER -> 31
                Month.NOVEMBER -> 30
                Month.DECEMBER -> 31
            }
        return LocalDate(year, month, daysInMonth)
    }

    /**
     * Checks if a year is a leap year.
     *
     * @param year The year to check
     * @return true if leap year, false otherwise
     */
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * Gets the previous month and year.
     *
     * @param year Current year
     * @param month Current month
     * @return Pair of (year, month) for the previous month
     */
    fun getPreviousMonth(year: Int, month: Month): Pair<Int, Month> {
        return if (month == Month.JANUARY) {
            Pair(year - 1, Month.DECEMBER)
        } else {
            Pair(year, Month.entries[month.ordinal - 1])
        }
    }

    /**
     * Gets the next month and year.
     *
     * @param year Current year
     * @param month Current month
     * @return Pair of (year, month) for the next month
     */
    fun getNextMonth(year: Int, month: Month): Pair<Int, Month> {
        return if (month == Month.DECEMBER) {
            Pair(year + 1, Month.JANUARY)
        } else {
            Pair(year, Month.entries[month.ordinal + 1])
        }
    }

    /**
     * Gets the display name for a month.
     *
     * @param month The month
     * @return Human-readable month name
     */
    fun getMonthDisplayName(month: Month): String {
        return when (month) {
            Month.JANUARY -> "January"
            Month.FEBRUARY -> "February"
            Month.MARCH -> "March"
            Month.APRIL -> "April"
            Month.MAY -> "May"
            Month.JUNE -> "June"
            Month.JULY -> "July"
            Month.AUGUST -> "August"
            Month.SEPTEMBER -> "September"
            Month.OCTOBER -> "October"
            Month.NOVEMBER -> "November"
            Month.DECEMBER -> "December"
        }
    }

    /**
     * Gets the short day names for the calendar header. Week starts on Monday.
     *
     * @return List of day abbreviations (Mon, Tue, Wed, ...)
     */
    fun getDayHeaders(): List<String> {
        return listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    }
}
