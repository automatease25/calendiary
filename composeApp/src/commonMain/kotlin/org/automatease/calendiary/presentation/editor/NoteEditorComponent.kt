package org.automatease.calendiary.presentation.editor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.automatease.calendiary.domain.error.DiaryError
import org.automatease.calendiary.domain.model.DiaryEntry
import org.automatease.calendiary.domain.repository.DiaryRepository

/** Interface for the note editor component. Uses Decompose Value for state. */
interface NoteEditorComponent {
    val state: Value<NoteEditorUiState>

    fun onEvent(event: NoteEditorUiEvent)

    fun onBackClick()

    fun saveOnDispose()
}

/**
 * Default implementation of NoteEditorComponent using Decompose. Uses Arrow Either for typed error
 * handling. Factories are manually created in AppComponent for assisted injection pattern.
 */
class DefaultNoteEditorComponent(
    componentContext: ComponentContext,
    private val date: LocalDate,
    private val onNavigateBack: () -> Unit,
    private val diaryRepository: DiaryRepository,
) : NoteEditorComponent, ComponentContext by componentContext {

    private val scope = coroutineScope()

    private val _state = MutableValue(createInitialState())
    override val state: Value<NoteEditorUiState> = _state

    private var autoSaveJob: Job? = null
    private var originalContent: String = ""

    companion object {
        private const val AUTO_SAVE_DELAY_MS = 1500L
    }

    init {
        loadEntry()
    }

    /** Creates the initial UI state. */
    private fun createInitialState(): NoteEditorUiState {
        return NoteEditorUiState(date = date, dateDisplayText = formatDateForDisplay(date))
    }

    /** Formats the date for display in the editor header. */
    private fun formatDateForDisplay(date: LocalDate): String {
        val dayName = getDayOfWeekName(date.dayOfWeek)
        val monthName = getMonthName(date.monthNumber)
        return "$dayName, $monthName ${date.dayOfMonth}, ${date.year}"
    }

    /** Gets the full name of the day of week. */
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

    /** Gets the full name of the month. */
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

    /** Handles UI events from the note editor screen. */
    override fun onEvent(event: NoteEditorUiEvent) {
        when (event) {
            is NoteEditorUiEvent.UpdateContent -> updateContent(event.content)
            is NoteEditorUiEvent.Save -> saveEntry()
            is NoteEditorUiEvent.Delete -> deleteEntry()
        }
    }

    override fun onBackClick() {
        onNavigateBack()
    }

    /** Loads the existing diary entry for the date using typed error handling. */
    private fun loadEntry() {
        scope.launch {
            val result = diaryRepository.getEntry(date)

            result.fold(
                ifLeft = { error ->
                    when (error) {
                        is DiaryError.NotFound -> {
                            // Entry doesn't exist yet - that's fine, start with empty content
                            originalContent = ""
                            _state.update { it.copy(content = "", isLoading = false) }
                        }
                        else -> {
                            // Handle other errors by showing error state
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = error.message,
                                )
                            }
                        }
                    }
                },
                ifRight = { entry ->
                    originalContent = entry.content
                    _state.update {
                        it.copy(
                            content = entry.content,
                            isLoading = false,
                            hasUnsavedChanges = false,
                        )
                    }
                },
            )
        }
    }

    /** Updates the content and triggers auto-save with debounce. */
    private fun updateContent(newContent: String) {
        _state.update {
            it.copy(content = newContent, hasUnsavedChanges = newContent != originalContent)
        }

        // Cancel previous auto-save job and start a new one
        autoSaveJob?.cancel()
        autoSaveJob =
            scope.launch {
                delay(AUTO_SAVE_DELAY_MS)
                if (_state.value.hasUnsavedChanges) {
                    saveEntry()
                }
            }
    }

    /** Saves the current entry to the repository using typed error handling. */
    private fun saveEntry() {
        val content = _state.value.content

        // Don't save empty content - delete instead if it was previously saved
        if (content.isBlank()) {
            if (originalContent.isNotBlank()) {
                deleteEntry()
            }
            return
        }

        scope.launch {
            _state.update { it.copy(isSaving = true) }

            val entry = DiaryEntry(date = date, content = content)
            val result = diaryRepository.saveEntry(entry)

            result.fold(
                ifLeft = { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message,
                        )
                    }
                },
                ifRight = {
                    originalContent = content
                    _state.update {
                        it.copy(
                            isSaving = false,
                            hasUnsavedChanges = false,
                            errorMessage = null,
                        )
                    }
                },
            )
        }
    }

    /** Deletes the diary entry for this date. */
    private fun deleteEntry() {
        scope.launch {
            _state.update { it.copy(isSaving = true) }

            val result = diaryRepository.deleteEntry(date)

            result.fold(
                ifLeft = { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message,
                        )
                    }
                },
                ifRight = {
                    originalContent = ""
                    _state.update {
                        it.copy(
                            content = "",
                            isSaving = false,
                            hasUnsavedChanges = false,
                            errorMessage = null,
                        )
                    }
                },
            )
        }
    }

    /** Saves any pending changes before the screen is disposed. Should be called from onDispose. */
    override fun saveOnDispose() {
        autoSaveJob?.cancel()
        if (_state.value.hasUnsavedChanges) {
            // Use a non-cancellable scope for final save
            scope.launch {
                val content = _state.value.content
                if (content.isNotBlank()) {
                    val entry = DiaryEntry(date = date, content = content)
                    diaryRepository.saveEntry(entry)
                } else if (originalContent.isNotBlank()) {
                    diaryRepository.deleteEntry(date)
                }
            }
        }
    }
}
