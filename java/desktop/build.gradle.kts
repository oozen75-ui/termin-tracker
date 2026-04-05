plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":common"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Rpm,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe
            )
            packageName = "TerminTracker"
            packageVersion = "1.0.1"
            description = "Almanya Randevu Takip Sistemi"
            vendor = "Technic Genius GmbH"
            copyright = "© 2026 Technic Genius GmbH"
            licenseFile.set(project.file("../../LICENSE.txt"))
            
            linux {
                debMaintainer = "technic-gnius@example.com"
                menuGroup = "Office"
                appRelease = "1"
                appCategory = "Office"
            }
            
            windows {
                menuGroup = "Termin Tracker"
                upgradeUuid = "a0b1c2d3-e4f5-6789-0123-456789abcdef"
                msiPackage = true
                shortcut = true
            }
            
            macOS {
                bundleID = "com.termintracker.desktop"
                appStore = false
            }
        }
    }
}
