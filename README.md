<div align="center">

[Tech Stack](#tech-stack) •
[Prerequisites](#prerequisites) •
[Project Structure](#project-structure) •
[Build & Run](#build--run)

</div>

---

## Tech Stack

This project uses a modern **Kotlin Multiplatform** stack to share code between Android and iOS:

| Category | Technology | Description |
|----------|------------|-------------|
| **Language** | [Kotlin](https://kotlinlang.org/) | Modern, concise, and safe programming language. |
| **UI Framework** | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) | Declarative UI framework shared across platforms. |
| **Navigation** | [Decompose](https://arkivanov.github.io/Decompose/) | Lifecycle-aware navigation library for Compose Multiplatform. |
| **DI** | [Koin](https://insert-koin.io/) | Lightweight dependency injection framework. |
| **Database** | [Room KMP](https://developer.android.com/kotlin/multiplatform/room) | SQLite object mapping library. |
| **Concurrency** | [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) | Library for asynchronous programming. |
| **Date & Time** | [Kotlinx DateTime](https://github.com/Kotlin/kotlinx-datetime) | Multiplatform date and time handling. |

## Prerequisites

To build and run this project, ensure your development environment meets the following requirements:

*   **JDK 17 or 21** (required for AGP 8+)
*   **Android Studio** (Ladybug or newer recommended) or **IntelliJ IDEA**
*   **Xcode** (required for building the iOS app; macOS only)
*   **Kotlin Multiplatform Mobile Plugin** (usually bundled with recent Android Studio versions)

## Project Structure

The codebase is organized into shared and platform-specific modules:

*   **`/composeApp`**: Code shared across Compose Multiplatform applications.
    *   `commonMain`: Code common to all targets.
    *   `androidMain`: Android-specific implementation.
    *   `iosMain`: iOS-specific implementation (e.g., Apple frameworks integration).
    *   `jvmMain`: Desktop (JVM) specific implementation.

*   **`/iosApp`**: The iOS application entry point. Contains the native shell and SwiftUI code required to bootstrap the shared UI.

## Build & Run

### Android Application

To build and run the development version of the Android app, use the run configuration in your IDE or build via terminal:

**Run on macOS / Linux**
```shell
./gradlew :composeApp:assembleDebug
```

**Run on Windows**
```shell
.\gradlew.bat :composeApp:assembleDebug
```

### iOS Application

To build and run the development version of the iOS app:

1.  Open the `/iosApp` directory in **Xcode**.
2.  Run the application from Xcode.
3.  Alternatively, use the run configuration from your IDE’s toolbar.

---

<div align="center">
  Learn more about <a href="https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html">Kotlin Multiplatform</a>.
</div>
