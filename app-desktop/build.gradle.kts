import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()
    jvmToolchain(libs.versions.java.get().toInt())

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared:ui"))
                implementation(compose.desktop.currentOs)
                implementation(compose.components.resources)
                implementation(libs.jetbrains.compose.material3.windowSizeClass)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "pl.pathfinding.app.desktop.AppKt"
        //jvmArgs += "-Duser.language=en"  // Simulate system language
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "pathfinding"
            packageVersion = "1.0.0"
        }
        buildTypes.release.proguard {
            version.set("7.6.0")
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}
