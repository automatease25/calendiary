package org.automatease.calendiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import org.automatease.calendiary.data.local.initializeDatabaseContext
import org.automatease.calendiary.di.getKoinModules
import org.automatease.calendiary.domain.repository.DiaryRepository
import org.automatease.calendiary.presentation.DefaultRootComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize database context
        initializeDatabaseContext(applicationContext)

        // Initialize Koin
        try {
            startKoin {
                androidLogger()
                androidContext(applicationContext)
                modules(getKoinModules())
            }
        } catch (e: IllegalStateException) {
            // Koin already started (e.g., configuration change)
        }

        // Get repository from Koin
        val diaryRepository = org.koin.core.context.GlobalContext.get().get<DiaryRepository>()

        // Create the root component before starting Compose
        val rootComponent =
            DefaultRootComponent(
                componentContext = defaultComponentContext(),
                diaryRepository = diaryRepository,
            )

        setContent { App(rootComponent) }
    }
}
