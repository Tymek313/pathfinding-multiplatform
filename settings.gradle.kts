rootProject.name = "pathfinding"
include(":app-android")
include(":app-desktop")
include(":shared:domain")
include(":shared:ui")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
    }
}