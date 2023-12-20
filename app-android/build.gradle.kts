plugins {
    alias(versions.plugins.agp.application)
    alias(versions.plugins.kotlin.android)
}

android {
    namespace = "pl.pathfinding"
    compileSdk = 34

    defaultConfig {
        applicationId = "pl.pathfinding"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = versions.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kotlin {
        jvmToolchain(versions.versions.java.get().toInt())
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(versions.androidx.lifecycle.runtime.ktx)
    implementation(versions.androidx.activity.compose)
    implementation(project.dependencies.platform(versions.androidx.compose.bom))
    implementation(versions.androidx.compose.ui)
    implementation(versions.androidx.compose.ui.graphics)
    implementation(versions.androidx.compose.ui.tooling.preview)
    implementation(versions.androidx.compose.material3)
    testImplementation(versions.junit)
    androidTestImplementation(project.dependencies.platform(versions.androidx.compose.bom))
    androidTestImplementation(versions.androidx.compose.ui.test.junit4)
    debugImplementation(versions.androidx.compose.ui.tooling)
    debugImplementation(versions.androidx.compose.ui.test.manifest)
}