package org.automatease.calendiary.presentation.editor

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.automatease.calendiary.domain.model.DiaryEntry
import org.automatease.calendiary.domain.repository.DiaryRepository

/**
 * ScreenModel for the note editor screen.
 * Handles loading, editing, and auto-saving diary entries.
 */
class NoteEditorScreenModel(
    private val date: LocalDate,
    private val diaryRepository: DiaryRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    private var autoSaveJob: Job? = null
    private var originalContent: String = ""

    companion object {
        private const val AUTO_SAVE_DELAY_MS = 1500L
    }

    init {
        loadEntry()
    }

    /**
     * Creates the initial UI state.
     */
    private fun createInitialState(): NoteEditorUiState {
        return NoteEditorUiState(
            date = date,
            dateDisplayText = formatDateForDisplay(date)
        )
    }

    /**
     * Formats the date for display in the editor header.
     */
    private fun formatDateForDisplay(date: LocalDate): String {
        val dayName = getDayOfWeekName(date.dayOfWeek)
        val monthName = getMonthName(date.monthNumber)
        return "$dayName, $monthName ${date.dayOfMonth}, ${date.year}"
    }

    /**
     * Gets the full name of the day of week.
     */
    private fun getDayOfWeekName(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Monday"
            DayOfWeek.TUESDAY -> "Tuesday"
            DayOfWeek.WEDNESDAY -> "Wednesday"
            DayOfWeek.THURSDAY -> "Thursday"
            DayOfWeek.FRIDAY -> "Friday"
            DayOfWeek.SATURDAY -> "Saturday"
            DayOfWeek.SUNDAY -> "Sunday"
        }
    }

    /**
     * Gets the full name of the month.
     */
    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }

    /**
     * Handles UI events from the note editor screen.
     */
    fun onEvent(event: NoteEditorUiEvent) {
        when (event) {
            is NoteEditorUiEvent.UpdateContent -> updateContent(event.content)
            is NoteEditorUiEvent.Save -> saveEntry()
            is NoteEditorUiEvent.Delete -> deleteEntry()
        }
    }

    /**
     * Loads the existing diary entry for the date.
     */
    private fun loadEntry() {
        screenModelScope.launch {
            val entry = diaryRepository.getEntry(date).first()
            originalContent = entry?.content ?: ""
            
            _uiState.update { currentState ->
                currentState.copy(
                    content = originalContent,
                    isLoading = false,
                    hasUnsavedChanges = false
                )
            }
        }
    }

    /**
     * Updates the content and triggers auto-save with debounce.
     */
    private fun updateContent(newContent: String) {
        _uiState.update { currentState ->
            currentState.copy(
                content = newContent,
                hasUnsavedChanges = newContent != originalContent
            )
        }

        // Cancel previous auto-save job and start a new one
        autoSaveJob?.cancel()
        autoSaveJob = screenModelScope.launch {
            delay(AUTO_SAVE_DELAY_MS)
            if (_uiState.value.hasUnsavedChanges) {
                saveEntry()
            }
        }
    }

    /**
     * Saves the current entry to the repository.
     */
    private fun saveEntry() {
        val content = _uiState.value.content
        
        // Don't save empty content - delete instead if it was previously saved
        if (content.isBlank()) {
            if (originalContent.isNotBlank()) {
                deleteEntry()
            }
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val entry = DiaryEntry(
                date = date,
                content = content
            )
            diaryRepository.saveEntry(entry)
            originalContent = content

            _uiState.update { currentState ->
                currentState.copy(
                    isSaving = false,
                    hasUnsavedChanges = false
                )
            }
        }
    }

    /**
     * Deletes the diary entry for this date.
     */
    private fun deleteEntry() {
        screenModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            diaryRepository.deleteEntry(date)
            originalContent = ""

            _uiState.update { currentState ->
                currentState.copy(
                    content = "",
                    isSaving = false,
                    hasUnsavedChanges = false
                )
            }
        }
    }

    /**
     * Saves any pending changes before the screen is disposed.
     * Should be called from onDispose.
     */
    fun saveOnDispose() {
        autoSaveJob?.cancel()
        if (_uiState.value.hasUnsavedChanges) {
            // Use a non-cancellable scope for final save
            screenModelScope.launch {
                val content = _uiState.value.content
                if (content.isNotBlank()) {
                    val entry = DiaryEntry(
                        date = date,
                        content = content
                    )
                    diaryRepository.saveEntry(entry)
                } else if (originalContent.isNotBlank()) {
                    diaryRepository.deleteEntry(date)
                }
            }
        }
    }
}
