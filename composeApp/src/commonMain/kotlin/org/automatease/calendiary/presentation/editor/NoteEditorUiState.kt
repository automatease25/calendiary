package org.automatease.calendiary.presentation.editor

import kotlinx.datetime.LocalDate

/** UI state for the note editor screen. */
data class NoteEditorUiState(
    val date: LocalDate,
    val content: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val dateDisplayText: String = "",
    val hasUnsavedChanges: Boolean = false,
)

/** Events that can be triggered from the note editor UI. */
sealed class NoteEditorUiEvent {
    data class UpdateContent(val content: String) : NoteEditorUiEvent()

    data object Save : NoteEditorUiEvent()

    data object Delete : NoteEditorUiEvent()
}
