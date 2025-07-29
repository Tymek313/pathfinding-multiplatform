import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.agp.library)
    kotlin("multiplatform")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        dependencies {
            androidTestImplementation(libs.androidx.ui.test.junit4.android)
            debugImplementation(libs.androidx.ui.test.manifest)
        }
    }
    jvm("desktop")
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
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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