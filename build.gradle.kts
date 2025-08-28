// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.12.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0" apply false
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

rootProject.file(".env").readLines().forEach { line ->
    if (line.isNotBlank()) {
        val splits = line.split("=")
        val env = splits[0] to splits.subList(1, splits.size).joinToString("")
        System.setProperty(env.first, env.second)
    }
}
