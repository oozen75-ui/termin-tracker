plugins {
    kotlin("jvm") version "1.9.22" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
    id("org.jetbrains.compose") version "1.6.2" apply false
}

allprojects {
    group = "com.termintracker"
    version = "1.0.1"

    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
