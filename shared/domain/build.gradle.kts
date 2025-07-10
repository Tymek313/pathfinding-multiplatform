plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
    jvm()
}