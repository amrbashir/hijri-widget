import me.amrbashir.hijriwidget.build.GenerateChangelogTask
import me.amrbashir.hijriwidget.build.GenerateContributorsTask

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
        versionCode = 22
        versionName = "1.1.2"
        buildConfigField("String", "GIT_SHA", "\"$gitSha\"")
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

tasks.register<GenerateContributorsTask>("generateGitHubContributors") {
    packageName.set("me.amrbashir.hijriwidget")
    outputFile.set(layout.buildDirectory.file("generated/source/contributors/me/amrbashir/hijriwidget/Contributors.kt"))
    resOutputDir.set(layout.buildDirectory.dir("generated/resources/drawable"))
}

tasks.register<GenerateChangelogTask>("generateChangelogFile") {
    changelogFile.set(rootProject.file("CHANGELOG.md"))
    packageName.set("me.amrbashir.hijriwidget")
    outputFile.set(layout.buildDirectory.file("generated/source/changelog/me/amrbashir/hijriwidget/Changelog.kt"))
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
