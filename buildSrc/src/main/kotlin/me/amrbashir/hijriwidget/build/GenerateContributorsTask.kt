package me.amrbashir.hijriwidget.build

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URI

abstract class GenerateContributorsTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val resOutputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val contributorsAPIUrl = "https://api.github.com/repos/amrbashir/hijri-widget/contributors"
        val response = URI(contributorsAPIUrl).toURL().readText()

        @Suppress("UNCHECKED_CAST")
        val contributors = JsonSlurper().parseText(response) as List<Map<String, Any>>

        val drawableDir = resOutputDir.get().asFile
        drawableDir.deleteRecursively()
        drawableDir.mkdirs()

        val entries = contributors.take(5).map { contributor ->
            val login = contributor["login"] as String
            val avatarUrl = contributor["avatar_url"] as String
            val url = contributor["html_url"] as String
            val contributions = contributor["contributions"] as Int
            val resourceName = "contributor_${login.replace('-', '_')}"

            val connection = URI(avatarUrl).toURL().openConnection()
            val contentType = connection.contentType ?: "image/png"
            val extension = when {
                contentType.contains("jpeg") || contentType.contains("jpg") -> "jpg"
                contentType.contains("webp") -> "webp"
                else -> "png" // fallback to png
            }

            connection.getInputStream().use { input ->
                val avatarFile = File(drawableDir, "$resourceName.$extension")
                avatarFile.outputStream().use { output -> input.copyTo(output) }
            }

            """Contributor(
        avatar = R.drawable.$resourceName,
        username = "$login",
        url = "$url",
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

        val kotlinFile = outputDir.file("me/amrbashir/hijriwidget/Contributors.kt").get().asFile
        kotlinFile.parentFile.mkdirs()
        kotlinFile.writeText(generatedCode)
    }
}
