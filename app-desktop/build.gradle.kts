import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
}

group = "pl.pathfinding"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":shared"))
    implementation(compose.material3)
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "pl.pathfinding.AppKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "pathfinding"
            packageVersion = "1.0.0"
        }
    }
}
