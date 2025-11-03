// build.gradle.kts (Module: app)
plugins {
    id("kotlin-kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // This is correct, using the alias
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}

android {
    namespace = "com.example.travelvault"
    compileSdk = 34 // Stable API

    defaultConfig {
        applicationId = "com.example.travelvault"
        minSdk = 24
        targetSdk = 34 // Stable API
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    }
    // composeOptions block is removed as it's handled by the kotlin.compose plugin
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- CLEANED UP COMPOSE DEPENDENCIES ---

    // 1. This Bill of Materials (BOM) manages all Compose versions.
    // It correctly uses the version from your libs.versions.toml file.
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Use the same BOM for tests

    // 2. These are the Compose libraries we need.
    implementation("androidx.activity:activity-compose")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")

    // 3. Test and Debug dependencies
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // --- END OF COMPOSE DEPENDENCIES ---


    // Room (Database)
    val room_version = "2.7.0-alpha02"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-common:$room_version")

    // Other essentials
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("com.google.android.material:material:1.12.0")
}
