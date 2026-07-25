plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.complexsoft.yadratrain"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.complexsoft.yadratrain"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(files("libs/yadra-train-release.aar"))

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI (already included)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // NEW: extended icons (for CIFAR-10)
    implementation(libs.androidx.compose.material.icons.extended)

    // Activity Compose
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // NEW: ViewModel in Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // NEW: Runtime Compose (for collectAsStateWithLifecycle)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Core KTX
    implementation(libs.androidx.core.ktx)

    // NEW: Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // NEW: DocumentFile (to save files)
    implementation(libs.androidx.documentfile)

    // Test... (what you already have)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}