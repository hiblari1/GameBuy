plugins {
    kotlin("multiplatform") version "2.1.0"
    id("org.jetbrains.compose") version "1.7.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3) // Base M3
                implementation(compose.materialIconsExtended)
                // Expressive M3 (Experimental)
                implementation("org.jetbrains.compose.material3:material3:1.9.0-alpha04")
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                
                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
                implementation("media.kamel:kamel-image:0.9.3")
                implementation("io.ktor:ktor-client-okhttp:2.3.12")
                
                // Force Skiko version to match Material 3 Alpha
                implementation("org.jetbrains.skiko:skiko:0.9.4.2")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                // Force Skiko runtime version
                implementation("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.9.4.2")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
