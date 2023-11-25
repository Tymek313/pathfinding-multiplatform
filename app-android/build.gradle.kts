plugins {
    alias(versions.plugins.agp)
    alias(versions.plugins.kotlin.android)
}

android {
    namespace = "pathfinding"
    compileSdk = 34

    defaultConfig {
        applicationId = "pathfinding"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(versions.versions.java.get().toInt())
    }
}

dependencies {
    implementation(project(":shared"))
    testImplementation(versions.junit)
}