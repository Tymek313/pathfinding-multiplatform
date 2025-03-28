plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    jvmToolchain(libs.versions.java.get().toInt())

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}