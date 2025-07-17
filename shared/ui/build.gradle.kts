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
                implementation(project(":shared:domain"))
                implementation(compose.ui)
                implementation(compose.uiTooling)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)
                implementation(libs.jetbrains.compose.material3.windowSizeClass)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

compose.resources {
    publicResClass = true
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "pl.pathfinding.shared"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/composeResources")
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