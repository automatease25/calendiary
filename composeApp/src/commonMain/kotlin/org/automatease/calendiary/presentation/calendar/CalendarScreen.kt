package org.automatease.calendiary.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import org.automatease.calendiary.domain.model.CalendarDay
import org.automatease.calendiary.presentation.editor.NoteEditorScreen

/** Main calendar screen displaying the monthly grid. */
class CalendarScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<CalendarScreenModel>()
        val uiState by screenModel.uiState.collectAsState()

        // Refresh when returning to this screen
        LaunchedEffect(Unit) { screenModel.refresh() }

        Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                // Month navigation header
                MonthHeader(
                    monthDisplayName = uiState.monthDisplayName,
                    year = uiState.currentYear,
                    onPreviousMonth = {
                        screenModel.onEvent(CalendarUiEvent.NavigateToPreviousMonth)
                    },
                    onNextMonth = { screenModel.onEvent(CalendarUiEvent.NavigateToNextMonth) },
                    onTodayClick = { screenModel.onEvent(CalendarUiEvent.NavigateToToday) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Day headers (Mon, Tue, Wed, ...)
                DayHeadersRow(dayHeaders = uiState.dayHeaders)

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar grid
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    uiState.calendarMonth?.let { calendarMonth ->
                        CalendarGrid(
                            days = calendarMonth.days,
                            onDayClick = { date -> navigator.push(NoteEditorScreen(date)) },
                        )
                    }
                }
            }
        }
    }
}

/** Month navigation header with previous/next buttons. */
@Composable
private fun MonthHeader(
    monthDisplayName: String,
    year: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = monthDisplayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
        }

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next month",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }

    // Today button
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        TextButton(onClick = onTodayClick) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/** Row displaying day abbreviations (Mon, Tue, Wed, ...). */
@Composable
private fun DayHeadersRow(dayHeaders: List<String>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        dayHeaders.forEach { dayName ->
            Text(
                text = dayName,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        }
    }
}

/** Calendar grid using LazyVerticalGrid. */
@Composable
private fun CalendarGrid(days: List<CalendarDay>, onDayClick: (LocalDate) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(days) { day -> CalendarDayCell(day = day, onClick = onDayClick) }
    }
}

/** Individual calendar day cell. */
@Composable
private fun CalendarDayCell(day: CalendarDay, onClick: (LocalDate) -> Unit) {
    when (day) {
        is CalendarDay.Day -> {
            DayCell(
                date = day.date,
                isToday = day.isToday,
                hasEntry = day.hasEntry,
                isCurrentMonth = true,
                onClick = { onClick(day.date) },
            )
        }
        is CalendarDay.PreviousMonthDay -> {
            DayCell(
                date = day.date,
                isToday = false,
                hasEntry = false,
                isCurrentMonth = false,
                onClick = { onClick(day.date) },
            )
        }
        is CalendarDay.NextMonthDay -> {
            DayCell(
                date = day.date,
                isToday = false,
                hasEntry = false,
                isCurrentMonth = false,
                onClick = { onClick(day.date) },
            )
        }
        is CalendarDay.Empty -> {
            Box(modifier = Modifier.aspectRatio(1f).fillMaxWidth())
        }
    }
}

/** Styled day cell with support for today highlight and entry indicator. */
@Composable
private fun DayCell(
    date: LocalDate,
    isToday: Boolean,
    hasEntry: Boolean,
    isCurrentMonth: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor =
        when {
            isToday -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.surface
        }

    val textColor =
        when {
            isToday -> MaterialTheme.colorScheme.onPrimary
            isCurrentMonth -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        }

    val borderModifier =
        if (isCurrentMonth && !isToday) {
            Modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp),
            )
        } else {
            Modifier
        }

    Box(
        modifier =
            Modifier.aspectRatio(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .then(borderModifier)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
            )

            // Entry indicator dot
            if (hasEntry) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier =
                        Modifier.size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isToday) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }))
            }
        }
    }
}
