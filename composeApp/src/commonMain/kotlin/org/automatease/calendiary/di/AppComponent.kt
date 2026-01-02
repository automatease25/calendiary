package org.automatease.calendiary.di

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDate
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.automatease.calendiary.data.local.DiaryDatabase
import org.automatease.calendiary.data.repository.RoomDiaryRepository
import org.automatease.calendiary.domain.repository.DiaryRepository
import org.automatease.calendiary.presentation.DefaultRootComponent
import org.automatease.calendiary.presentation.RootComponent
import org.automatease.calendiary.presentation.calendar.CalendarComponent
import org.automatease.calendiary.presentation.calendar.DefaultCalendarComponent
import org.automatease.calendiary.presentation.editor.DefaultNoteEditorComponent
import org.automatease.calendiary.presentation.editor.NoteEditorComponent

/**
 * Root dependency injection component using kotlin-inject. Provides compile-time verified
 * dependency graph with no runtime reflection.
 *
 * Platform-specific subcomponents (AndroidAppComponent, IosAppComponent) extend this and provide
 * the DiaryDatabase.
 */
@Component
abstract class AppComponent(
    @get:Provides val database: DiaryDatabase,
) {
    /** Provides the IO dispatcher for background operations. */
    @Provides fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /** Provides the DiaryRepository implementation. */
    @Provides
    fun provideDiaryRepository(
        database: DiaryDatabase,
        ioDispatcher: CoroutineDispatcher,
    ): DiaryRepository = RoomDiaryRepository(database, ioDispatcher)

    /** Access to the repository for creating factories. */
    protected abstract val diaryRepository: DiaryRepository

    /** Factory for creating CalendarComponent with assisted ComponentContext. */
    val calendarComponentFactory: CalendarComponentFactory
        get() = { componentContext, onNavigateToEditor ->
            DefaultCalendarComponent(
                componentContext = componentContext,
                onNavigateToEditor = onNavigateToEditor,
                diaryRepository = diaryRepository,
            )
        }

    /** Factory for creating NoteEditorComponent with assisted ComponentContext and date. */
    val noteEditorComponentFactory: NoteEditorComponentFactory
        get() = { componentContext, date, onNavigateBack ->
            DefaultNoteEditorComponent(
                componentContext = componentContext,
                date = date,
                onNavigateBack = onNavigateBack,
                diaryRepository = diaryRepository,
            )
        }

    /** Factory for creating RootComponent with assisted ComponentContext. */
    val rootComponentFactory: RootComponentFactory
        get() = { componentContext ->
            DefaultRootComponent(
                componentContext = componentContext,
                calendarFactory = calendarComponentFactory,
                noteEditorFactory = noteEditorComponentFactory,
            )
        }
}

/** Factory interface for creating RootComponent instances with assisted injection. */
typealias RootComponentFactory = (componentContext: ComponentContext) -> RootComponent

/** Factory interface for creating CalendarComponent instances with assisted injection. */
typealias CalendarComponentFactory =
    (
        componentContext: ComponentContext,
        onNavigateToEditor: (LocalDate) -> Unit,
    ) -> CalendarComponent

/** Factory interface for creating NoteEditorComponent instances with assisted injection. */
typealias NoteEditorComponentFactory =
    (
        componentContext: ComponentContext,
        date: LocalDate,
        onNavigateBack: () -> Unit,
    ) -> NoteEditorComponent

/**
 * Extension function to create RootComponent using the factory. Provides a cleaner API for
 * component creation.
 */
fun AppComponent.createRootComponent(componentContext: ComponentContext): RootComponent =
    rootComponentFactory(componentContext)

/**
 * Extension function to create CalendarComponent using the factory. Provides a cleaner API for
 * component creation.
 */
fun AppComponent.createCalendarComponent(
    componentContext: ComponentContext,
    onNavigateToEditor: (LocalDate) -> Unit,
): CalendarComponent = calendarComponentFactory(componentContext, onNavigateToEditor)

/**
 * Extension function to create NoteEditorComponent using the factory. Provides a cleaner API for
 * component creation.
 */
fun AppComponent.createNoteEditorComponent(
    componentContext: ComponentContext,
    date: LocalDate,
    onNavigateBack: () -> Unit,
): NoteEditorComponent = noteEditorComponentFactory(componentContext, date, onNavigateBack)
