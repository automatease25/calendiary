package org.automatease.calendiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import org.automatease.calendiary.di.AndroidAppComponent
import org.automatease.calendiary.di.create
import org.automatease.calendiary.di.createRootComponent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Create the kotlin-inject component graph
        val appComponent = AndroidAppComponent::class.create(applicationContext)

        // Create the root component using the DI-provided factory
        val rootComponent = appComponent.createRootComponent(defaultComponentContext())

        setContent { App(rootComponent) }
    }
}
