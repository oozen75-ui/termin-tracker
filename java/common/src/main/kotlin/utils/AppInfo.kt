package com.termintracker.utils

object AppInfo {
    const val VERSION = "1.0.2"
    const val NAME = "Termin Tracker"
    const val FULL_NAME = "$NAME v$VERSION"
    const val AUTHOR = "Technic Genius GmbH"
    const val COPYRIGHT = "© 2026 $AUTHOR"
    
    fun getBuildInfo(): String {
        return """
            $FULL_NAME
            Kotlin: ${KotlinVersion.CURRENT}
            OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")}
            Java: ${System.getProperty("java.version")}
        """.trimIndent()
    }
}