package org.automatease.calendiary

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.automatease.calendiary.data.local.createDatabase
import org.automatease.calendiary.data.repository.RoomDiaryRepository
import org.automatease.calendiary.presentation.DefaultRootComponent
import org.automatease.calendiary.presentation.RootComponent
import org.automatease.calendiary.presentation.calendar.DefaultCalendarComponent
import org.automatease.calendiary.presentation.editor.DefaultNoteEditorComponent

/** Lazy holder for the iOS dependency graph. */
private val iosGraph: IosGraph by lazy { IosGraph() }

/**
 * Simple iOS dependency container. kotlin-inject's create() extension is generated per target, so
 * for iOS we manually construct the graph.
 */
private class IosGraph {
    private val database = createDatabase()
    private val ioDispatcher = Dispatchers.IO
    val diaryRepository = RoomDiaryRepository(database, ioDispatcher)
}

/** Creates the root component for iOS. This should be called before MainViewController(). */
fun createRootComponent(lifecycle: LifecycleRegistry): RootComponent {
    val componentContext = DefaultComponentContext(lifecycle = lifecycle)
    val repository = iosGraph.diaryRepository

    val calendarFactory =
        { ctx: com.arkivanov.decompose.ComponentContext, onNav: (kotlinx.datetime.LocalDate) -> Unit
            ->
            DefaultCalendarComponent(ctx, onNav, repository)
        }

    val noteEditorFactory =
        {
            ctx: com.arkivanov.decompose.ComponentContext,
            date: kotlinx.datetime.LocalDate,
            onBack: () -> Unit ->
            DefaultNoteEditorComponent(ctx, date, onBack, repository)
        }

    return DefaultRootComponent(
        componentContext = componentContext,
        calendarFactory = calendarFactory,
        noteEditorFactory = noteEditorFactory,
    )
}

fun MainViewController(component: RootComponent) = ComposeUIViewController { App(component) }
