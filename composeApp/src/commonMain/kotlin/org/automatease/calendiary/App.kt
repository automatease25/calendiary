package org.automatease.calendiary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.automatease.calendiary.presentation.RootComponent
import org.automatease.calendiary.presentation.calendar.CalendarScreen
import org.automatease.calendiary.presentation.editor.NoteEditorScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(component: RootComponent) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Children(
                stack = component.childStack,
                modifier = Modifier.fillMaxSize(),
                animation = stackAnimation(fade() + slide()),
            ) { child ->
                when (val instance = child.instance) {
                    is RootComponent.Child.Calendar -> CalendarScreen(instance.component)
                    is RootComponent.Child.NoteEditor -> NoteEditorScreen(instance.component)
                }
            }
        }
    }
}
