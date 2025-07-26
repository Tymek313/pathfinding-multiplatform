plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
    jvm()

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.mockk)
            }
        }
    }
}