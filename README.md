## Tech Stack

This project uses a modern Kotlin Multiplatform stack to share code between Android and iOS:

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) - Declarative UI framework shared across platforms.
*   **Navigation:** [Voyager](https://github.com/adrielcafe/voyager) - A navigation library for Compose.
*   **Dependency Injection:** [Koin](https://insert-koin.io/) - A lightweight dependency injection framework for Kotlin.
*   **Database:** [Room KMP](https://developer.android.com/kotlin/multiplatform/room) - SQLite object mapping library.
*   **Concurrency:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - For asynchronous programming.
*   **Date & Time:** [Kotlinx DateTime](https://github.com/Kotlin/kotlinx-datetime) - A multiplatform date and time library.

### Prerequisites

To build and run this project, you will need:

*   **JDK 17/21** (required for AGP 8+).
*   **Android Studio** (Ladybug or newer recommended) or **IntelliJ IDEA**.
*   **Xcode** (required for building the iOS app; macOS only).
*   **Kotlin Multiplatform Mobile Plugin** (usually bundled with recent Android Studio versions).

### Project Structure

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…