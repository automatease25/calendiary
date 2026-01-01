package org.automatease.calendiary

import androidx.compose.ui.window.ComposeUIViewController
import org.automatease.calendiary.di.getKoinModules
import org.koin.core.context.startKoin

/**
 * Initialize Koin for iOS platform. This should be called from Swift before creating the
 * ComposeUIViewController.
 */
fun initKoin() {
    startKoin { modules(getKoinModules()) }
}

fun MainViewController() = ComposeUIViewController { App() }
