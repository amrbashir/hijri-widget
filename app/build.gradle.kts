plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "1.9.23"
}

android {
    namespace = "me.amrbashir.hijriwidget"
    compileSdk = 36

    defaultConfig {
        applicationId = "me.amrbashir.hijriwidget"
        minSdk = 26
        targetSdk = 36
        versionCode = 14
        versionName = "0.11.2"
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
            signingConfig = signingConfigs.getByName("config")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.work:work-runtime-ktx:2.10.3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.glance:glance-appwidget:1.2.0-alpha01")
    implementation("androidx.glance:glance-material3:1.2.0-alpha01")
    implementation("androidx.navigation:navigation-compose:2.9.3")
    implementation("com.github.skydoves:colorpicker-compose:1.1.2")
}
