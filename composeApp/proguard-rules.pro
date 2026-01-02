# ProGuard Rules for Calendiary

# Kotlin
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# KotlinX Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class org.automatease.calendiary.**$$serializer { *; }
-keepclassmembers class org.automatease.calendiary.** {
    *** Companion;
}
-keepclasseswithmembers class org.automatease.calendiary.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Arrow Core
-dontwarn arrow.**
-keep class arrow.** { *; }
-keepclassmembers class * {
    @arrow.core.<methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}

# Decompose
-keep class com.arkivanov.decompose.** { *; }
-dontwarn com.arkivanov.decompose.**

# Essenty
-keep class com.arkivanov.essenty.** { *; }
-dontwarn com.arkivanov.essenty.**

# KotlinX DateTime
-keep class kotlinx.datetime.** { *; }
-dontwarn kotlinx.datetime.**

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
