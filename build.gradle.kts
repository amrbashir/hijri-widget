// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	id("com.android.application") version "8.13.0" apply false
	id("org.jlleitschuh.gradle.ktlint") version "14.2.0" apply false
	id("org.jetbrains.kotlin.android") version "2.2.20" apply false
	id("org.jetbrains.kotlin.plugin.compose") version "2.2.20" apply false
}

// Setup environment variables from .env files
rootProject.file(".env").takeIf { it.exists() }?.readLines()?.forEach { line ->
	if (line.isNotBlank()) {
		val splits = line.split("=")
		val env = splits[0] to splits.subList(1, splits.size).joinToString("")
		System.setProperty(env.first, env.second)
	}
}

allprojects {
	apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

tasks.register("fmt") {
	dependsOn("ktlintFormat", ":app:ktlintFormat")
}

tasks.register("fmtCheck") {
	dependsOn("ktlintCheck", ":app:ktlintCheck")
}
