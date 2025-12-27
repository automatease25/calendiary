package org.automatease.calendiary

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform