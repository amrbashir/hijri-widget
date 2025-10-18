import groovy.json.JsonSlurper
import java.net.URI

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val gitSha = "git rev-parse --short HEAD".executeCommand()

android {
    namespace = "me.amrbashir.hijriwidget"
    compileSdk = 36

    defaultConfig {

        applicationId = "me.amrbashir.hijriwidget"
        minSdk = 26
        targetSdk = 36
        versionCode = 21
        versionName = "1.1.1"
        buildConfigField("String", "GIT_SHA", "\"${gitSha}\"")
    }

    signingConfigs {
        create("config") {
            storeFile = rootProject.file(System.getProperty("KEYSTORE_FILE_PATH"))
            storePassword = System.getProperty("KEYSTORE_PASSWORD")
            keyAlias = System.getProperty("KEY_ALIAS")
            keyPassword = System.getProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("config")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets.getByName("main") {
        kotlin.srcDir("build/generated/source")
        res.srcDir("build/generated/resources")
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.10.00"))
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.activity:activity-ktx:1.11.0")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.glance:glance-appwidget:1.2.0-beta01")
    implementation("androidx.glance:glance-material3:1.2.0-beta01")
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("com.github.jeziellago:compose-markdown:0.5.7")
    implementation("com.godaddy.android.colorpicker:compose-color-picker:0.7.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}

// Download top 5 contributors from GitHub and generate a kotlin file containing
// their avatars and usernames.
tasks.register("generateGitHubContributors") {
    doFirst {
        val contributorsAPIUrl = "https://api.github.com/repos/amrbashir/hijri-widget/contributors"
        val response = URI(contributorsAPIUrl).toURL().readText()

        @Suppress("UNCHECKED_CAST")
        val contributors = JsonSlurper().parseText(response) as List<Map<String, Any>>

        val drawableDir = project.file("build/generated/resources/drawable")
        drawableDir.deleteRecursively()
        drawableDir.mkdirs()

        val entries = contributors.take(5).map { contributor ->
            val login = contributor["login"] as String
            val avatarUrl = contributor["avatar_url"] as String
            val contributions = contributor["contributions"] as Int
            val resourceName = "contributor_${login.replace('-', '_')}"

            URI(avatarUrl).toURL().openStream().use { input ->
                val avatarFile = File(drawableDir, "$resourceName.png")
                avatarFile.outputStream().use { output -> input.copyTo(output) }
            }

            """Contributor(
        avatar = R.drawable.$resourceName,
        username = "$login",
        url = "https://github.com/$login",
        contributions = $contributions,
    )"""
        }

        val generatedCode = """/**
 * Automatically generated file. DO NOT MODIFY
 */
package me.amrbashir.hijriwidget

import androidx.annotation.DrawableRes

data class Contributor(
    @param:DrawableRes val avatar: Int,
    val username: String,
    val url: String,
    val contributions: Int,
)

val CONTRIBUTORS = listOf<Contributor>(
    ${entries.joinToString(",\n    ")}
)
"""

        val contributorsKotlinFile =
            "build/generated/source/contributors/me/amrbashir/hijriwidget/Contributors.kt"
        val outputFile = project.file(contributorsKotlinFile)
        outputFile.parentFile.mkdirs()
        outputFile.writeText(generatedCode)
    }
}


// Generate Changelog file containing last 10 releases from CHANGELOG.md
// which are embedded and displayed in the app
tasks.register("generateChangelogFile") {
    doFirst {
        val fileContent = file("../CHANGELOG.md").readText()
            .replace("# Changelog", "")
            .trimStart()

        /** Pattern to find `## [1.0.1] - 2025-08-29\n <Release Changelog>` */
        val versionPattern = Regex(
            """^##\s*\[([^]]+)]\s*-\s*(\d{4}-\d{2}-\d{2})((?s:.*?))(?=^##\s*\[|\Z)""",
            RegexOption.MULTILINE
        )

        val matches = versionPattern.findAll(fileContent).toList().take(10)

        val entries = mutableListOf<String>()

        // Handle optional "Unreleased" section
        val unreleasedHeader = "## [Unreleased]"
        if (fileContent.startsWith(unreleasedHeader)) {
            val start = unreleasedHeader.length + 1
            val end = matches.first().range.first
            val unreleasedContent = fileContent.substring(start, end).trim()
            if (unreleasedContent.isNotEmpty()) {
                entries.add(
                    """ChangelogEntry(
        header = "Unreleased",
        content = ""${'"'}$unreleasedContent""${'"'},
)"""
                )
            }
        }

        // Handle versioned sections
        matches.forEach { match ->
            val version = match.groupValues[1]
            val date = match.groupValues[2]
            val content = match.groupValues[3].trim()
            entries.add(
                """ChangelogEntry(
        header = "$version - $date",
        content = ""${'"'}$content""${'"'},
)"""
            )
        }

        val generatedCode = """/**
 * Automatically generated file. DO NOT MODIFY
 */
package me.amrbashir.hijriwidget

data class ChangelogEntry(
    val header: String,
    val content: String
)

val CHANGELOG = arrayOf<ChangelogEntry>(
    ${entries.joinToString(",\n    ")}
)"""

        val changelogKotlinFile =
            "build/generated/source/changelog/me/amrbashir/hijriwidget/CHANGELOG.kt"
        val outputFile = project.file(changelogKotlinFile)
        outputFile.parentFile.mkdirs()
        outputFile.writeText(generatedCode)
    }
}

tasks.named("preBuild") {
    dependsOn("generateGitHubContributors", "generateChangelogFile")
}


// Utilities
fun String.executeCommand(workingDir: File = project.rootDir): String {
    val process = ProcessBuilder(*this.split(" ").toTypedArray())
        .directory(workingDir)
        .redirectErrorStream(true)
        .start()
    return process.inputStream.bufferedReader().readText().trim()
}
