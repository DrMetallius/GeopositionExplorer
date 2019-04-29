plugins {
	id("com.android.application")
	kotlin("android")
}

val kotlinVersion: String by rootProject.extra

android {
	compileSdkVersion(28)
	defaultConfig {
		applicationId = "com.malcolmsoft.geopositionexplorer"
		minSdkVersion(23)
		targetSdkVersion(28)
		versionCode = 1
		versionName = "1.0.0"
	}

	compileOptions {
		targetCompatibility = JavaVersion.VERSION_1_8
		sourceCompatibility = JavaVersion.VERSION_1_8
	}
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1")

	implementation("androidx.core:core-ktx:1.0.1")
	implementation("androidx.appcompat:appcompat:1.0.2")
	implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
	implementation("androidx.recyclerview:recyclerview:1.0.0")

	implementation("com.squareup.okhttp3:okhttp:3.14.1")
	implementation("com.google.code.gson:gson:2.8.5")
}
