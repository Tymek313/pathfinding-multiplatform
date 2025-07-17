plugins {
    kotlin("multiplatform")
    alias(libs.plugins.agp.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    androidTarget()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared:ui"))
                implementation(compose.foundation)
                implementation(compose.components.resources)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.activity)
                implementation(libs.jetbrains.compose.material3.windowSizeClass)
                implementation(libs.material)
            }
        }
    }

    jvmToolchain(libs.versions.java.get().toInt())
}

android {
    namespace = "pl.pathfinding.app.android"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "pl.pathfinding"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}