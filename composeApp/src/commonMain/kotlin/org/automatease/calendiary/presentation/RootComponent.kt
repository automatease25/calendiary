package org.automatease.calendiary.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.automatease.calendiary.di.CalendarComponentFactory
import org.automatease.calendiary.di.NoteEditorComponentFactory
import org.automatease.calendiary.presentation.calendar.CalendarComponent
import org.automatease.calendiary.presentation.editor.NoteEditorComponent

/** Root component interface that manages the navigation stack. */
interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>

    /** Defines all possible child components in the navigation stack. */
    sealed class Child {
        data class Calendar(val component: CalendarComponent) : Child()

        data class NoteEditor(val component: NoteEditorComponent) : Child()
    }
}

/**
 * Default implementation of RootComponent using Decompose's child stack navigation. Uses factory
 * functions for child component creation to support the assisted injection pattern.
 */
class DefaultRootComponent(
    componentContext: ComponentContext,
    private val calendarFactory: CalendarComponentFactory,
    private val noteEditorFactory: NoteEditorComponentFactory,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Calendar,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            is Config.Calendar -> RootComponent.Child.Calendar(calendarComponent(componentContext))
            is Config.NoteEditor ->
                RootComponent.Child.NoteEditor(noteEditorComponent(componentContext, config.date))
        }
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun calendarComponent(componentContext: ComponentContext): CalendarComponent {
        return calendarFactory(
            componentContext,
            { date -> navigation.push(Config.NoteEditor(date)) },
        )
    }

    private fun noteEditorComponent(
        componentContext: ComponentContext,
        date: LocalDate,
    ): NoteEditorComponent {
        return noteEditorFactory(
            componentContext,
            date,
            { navigation.pop() },
        )
    }

    /** Serializable configuration for navigation state preservation. */
    @Serializable
    private sealed interface Config {
        @Serializable data object Calendar : Config

        @Serializable data class NoteEditor(val date: LocalDate) : Config
    }
}

/** Factory type alias for creating RootComponent instances with assisted injection. */
typealias RootComponentFactory = (ComponentContext) -> RootComponent
