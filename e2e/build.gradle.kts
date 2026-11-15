plugins {
	id("com.android.test")
}

android {
	namespace = "me.amrbashir.hijriwidget.e2e.test"
	compileSdk = 37
	targetProjectPath = ":app"

	defaultConfig {
		minSdk = 26
		targetSdk = 36
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		// Tests the app's minified `e2e` build type, so R8 issues show up like in release
		create("e2e") {
			signingConfig = signingConfigs.getByName("debug")
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}

	// Runs the tests in their own process, black-box, so they don't load the minified app's classes
	@Suppress("UnstableApiUsage")
	experimentalProperties["android.experimental.self-instrumenting"] = true
}

androidComponents {
	beforeVariants { it.enable = it.buildType == "e2e" }
}

dependencies {
	implementation("androidx.test:runner:1.7.0")
	implementation("androidx.test.ext:junit:1.3.0")
}
