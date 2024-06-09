// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1" apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        filter {
            exclude("*.gradle.kts")
            exclude {
                it.file.path.contains("${layout.buildDirectory}/generated/")
            }
        }
    }
}