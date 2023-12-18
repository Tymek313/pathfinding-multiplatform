plugins {
    alias(versions.plugins.compose)
    alias(versions.plugins.agp.library)
    alias(versions.plugins.kotlin.multiplatform)
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                api("dev.chrisbanes.material3:material3-window-size-class-multiplatform:0.3.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-junit"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(project.dependencies.platform("androidx.compose:compose-bom:2023.03.00"))
                implementation("androidx.compose.ui:ui-tooling")
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "pl.pathfinding.shared"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    defaultConfig {
        minSdk = 24
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    kotlin {
        jvmToolchain(17)
    }
}