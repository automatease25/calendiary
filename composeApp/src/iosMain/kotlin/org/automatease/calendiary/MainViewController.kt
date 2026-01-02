package org.automatease.calendiary

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.automatease.calendiary.di.getKoinModules
import org.automatease.calendiary.domain.repository.DiaryRepository
import org.automatease.calendiary.presentation.DefaultRootComponent
import org.koin.core.context.startKoin

/**
 * Initialize Koin for iOS platform. This should be called from Swift before creating the
 * ComposeUIViewController.
 */
fun initKoin() {
    startKoin { modules(getKoinModules()) }
}

/**
 * Creates the root component for iOS. This should be called after initKoin() and before
 * MainViewController().
 */
fun createRootComponent(lifecycle: LifecycleRegistry): DefaultRootComponent {
    val diaryRepository = org.koin.core.context.GlobalContext.get().get<DiaryRepository>()
    return DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        diaryRepository = diaryRepository,
    )
}

fun MainViewController(component: DefaultRootComponent) = ComposeUIViewController { App(component) }
