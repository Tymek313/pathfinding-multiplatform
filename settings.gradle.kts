rootProject.name = "pathfinding"
include(":app-android")
include(":app-desktop")
include(":shared")
include(":pathfinding-common")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
    }
}