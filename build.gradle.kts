plugins {
    alias(versions.plugins.kotlin.jvm) apply false
    alias(versions.plugins.kotlin.multiplatform) apply false
    alias(versions.plugins.compose) apply false
    alias(versions.plugins.agp) apply false
    alias(versions.plugins.agp.library) apply false
    alias(versions.plugins.kotlin.android) apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}