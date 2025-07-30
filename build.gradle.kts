import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version libs.versions.kotlin.get() apply false
    kotlin("multiplatform") version libs.versions.kotlin.get() apply false
    alias(libs.plugins.agp.application) apply false
    alias(libs.plugins.agp.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.7.1")
        verbose = true
        android = true
        reporters {
            reporter(ReporterType.HTML)
        }
        filter {
            exclude { element ->
                element.file.path.contains("${File.separator}generated${File.separator}")
            }
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}