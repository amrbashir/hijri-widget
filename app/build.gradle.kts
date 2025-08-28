plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.2.10"
}


android {
    namespace = "me.amrbashir.hijriwidget"
    compileSdk = 36

    defaultConfig {
        val gitSha = "git rev-parse --short HEAD".execute(project.rootDir)

        applicationId = "me.amrbashir.hijriwidget"
        minSdk = 26
        targetSdk = 36
        versionCode = 15
        versionName = "0.11.2"
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
            isMinifyEnabled = false
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
    }
}

tasks.register("generateChangelogFile") {
    doLast {
        val fileContent = file("../CHANGELOG.md").readText()

        val generatedCode = """
            package me.amrbashir.hijriwidget

            val CHANGELOG = ""${'"'}$fileContent""${'"'}
        """.trimIndent()

        val outputFile = file("build/generated/source/me/amrbashir/hijriwidget/CHANGELOG.kt")
        println(outputFile)
        outputFile.parentFile.mkdirs()
        outputFile.writeText(generatedCode)
    }
}

tasks.named("preBuild") {
    dependsOn("generateChangelogFile")
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.08.01"))
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.work:work-runtime-ktx:2.10.3")
    implementation("androidx.glance:glance-appwidget:1.2.0-beta01")
    implementation("androidx.glance:glance-material3:1.2.0-beta01")
    implementation("androidx.navigation:navigation-compose:2.9.3")
    implementation("com.github.skydoves:colorpicker-compose:1.1.2")
    implementation("com.github.jeziellago:compose-markdown:0.5.7")

    debugImplementation("androidx.compose.ui:ui-tooling")
}

fun String.execute(workingDir: File = rootDir): String {
    val process = ProcessBuilder(*this.split(" ").toTypedArray())
        .directory(workingDir)
        .redirectErrorStream(true)
        .start()
    return process.inputStream.bufferedReader().readText().trim()
}
