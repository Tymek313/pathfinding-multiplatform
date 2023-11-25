import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(versions.plugins.kotlin.jvm)
    alias(versions.plugins.compose)
}

group = "pathfinding"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "pathfinding"
            packageVersion = "1.0.0"
        }
    }
}
