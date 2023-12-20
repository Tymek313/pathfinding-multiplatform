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
                implementation(project(":pathfinding-common"))
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                api(versions.material3.windowsizeclass)
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
                implementation(project.dependencies.platform(versions.androidx.compose.bom))
                implementation(versions.androidx.compose.ui.tooling)
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
        kotlinCompilerExtensionVersion = versions.versions.composeCompiler.get()
    }
    kotlin {
        jvmToolchain(versions.versions.java.get().toInt())
    }
}