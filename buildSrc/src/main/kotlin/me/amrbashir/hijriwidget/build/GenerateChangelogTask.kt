package me.amrbashir.hijriwidget.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateChangelogTask : DefaultTask() {

    @get:InputFile
    abstract val changelogFile: RegularFileProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val fileContent = changelogFile.get().asFile.readText()
            .replace("# Changelog", "")
            .trimStart()

        /** Pattern to find `## [1.0.1] - 2025-08-29\n <Release Changelog>` */
        val versionPattern = Regex(
            """^##\s*\[([^]]+)]\s*-\s*(\d{4}-\d{2}-\d{2})((?s:.*?))(?=^##\s*\[|\Z)""",
            RegexOption.MULTILINE
        )

        val matches = versionPattern.findAll(fileContent).toList().take(10)

        val entries = buildList {
            // Handle optional "Unreleased" section
            val unreleasedHeader = "## [Unreleased]"
            if (fileContent.startsWith(unreleasedHeader)) {
                val end =
                    if (matches.isNotEmpty()) matches.first().range.first else fileContent.length
                val unreleasedContent = fileContent.substring(unreleasedHeader.length, end).trim()
                if (unreleasedContent.isNotEmpty()) {
                    add(
                        """ChangelogEntry(
        header = "Unreleased",
        content = ""${'"'}$unreleasedContent""${'"'}
    )"""
                    )
                }
            }

            // Handle versioned sections
            matches.forEach { match ->
                val version = match.groupValues[1]
                val date = match.groupValues[2]
                val content = match.groupValues[3].trim()
                add(
                    """ChangelogEntry(
        header = "$version - $date",
        content = ""${'"'}$content""${'"'}
    )"""
                )
            }
        }

        val generatedCode = """/**
 * Automatically generated file. DO NOT MODIFY
 */
package ${packageName.get()}

data class ChangelogEntry(
    val header: String,
    val content: String
)

val CHANGELOG = listOf<ChangelogEntry>(
    ${entries.joinToString(",\n    ")}
)"""

        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(generatedCode)
    }
}
