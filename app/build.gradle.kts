import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.9.23"
}

android {
    namespace = "me.amrbashir.hijriwidget"
  compileSdk = 35

  defaultConfig {
        applicationId = "me.amrbashir.hijriwidget"
        minSdk = 26
        targetSdk = 35
        versionCode = 9
        versionName = "0.9.0"
    }

    signingConfigs {
        create("config") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("config")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.glance:glance-appwidget:1.1.0")
    implementation("androidx.glance:glance-material3:1.1.0")
    implementation("androidx.navigation:navigation-compose:2.8.1")
    implementation("com.github.skydoves:colorpicker-compose:1.0.8")
}
