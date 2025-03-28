plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.agp.library)
    kotlin("multiplatform")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget()
    jvm()
    jvmToolchain(libs.versions.java.get().toInt())

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":pathfinding-common"))
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(libs.jetbrains.compose.material3.windowSizeClass)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.androidx.compose.bom))
                implementation(libs.androidx.compose.ui.tooling)
            }
        }
    }
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "pl.pathfinding.shared"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    kotlin {
        jvmToolchain(libs.versions.java.get().toInt())
    }
}