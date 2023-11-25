rootProject.name = "pathfinding"
include(":app-android")
include(":app-desktop")
include(":shared")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("versions") {
            from(files("versions.toml"))
        }
    }
}