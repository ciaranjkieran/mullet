plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.grogolden.mullet"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.grogolden.mullet"
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Hilt Core Dependencies
    implementation(libs.hilt.android)
    ksp(libs.androidx.hilt.compiler) // ✅ Hilt Compiler (was dagger.hilt.android.compiler)

    // Hilt WorkManager Integration
    implementation(libs.androidx.hilt.work)
    ksp(libs.dagger.hilt.android.compiler) // ✅ Fixes missing Hilt compiler

    // Assisted Injection (Required for WorkManager)
    implementation(libs.assisted.inject.annotations.dagger2)
    ksp(libs.assisted.inject.processor.dagger2) // ✅ Ensures Assisted Injection works

    // WorkManager Core
    implementation(libs.androidx.work.runtime.ktx)

    // Room Database (Using KSP)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler) // ✅ Using KSP for Room
    implementation(libs.androidx.room.ktx)

    // Retrofit Dependencies
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose core libraries
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.foundation)

    // Compose BOM (Bill of Materials) for consistent Compose versioning
    implementation(platform(libs.androidx.compose.bom))

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Firebase (if needed)
    implementation(libs.firebase.perf.ktx)

    // Hilt Navigation Compose
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.accompanist.systemuicontroller)


    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging tools
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

