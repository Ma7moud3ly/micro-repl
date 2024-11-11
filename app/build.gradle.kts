plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

// apply gms & firebase plugin only for gms build flavour
if (gradle.startParameter.taskRequests.toString().contains("gms", ignoreCase = true)) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
    apply(plugin = libs.plugins.firebase.crashlytics.get().pluginId)
}


android {
    namespace = "micro.repl.ma7moud3ly"
    compileSdk = 34

    defaultConfig {
        applicationId = "micro.repl.ma7moud3ly"
        minSdk = 24
        targetSdk = 34
        versionCode = 13
        versionName = "1.8"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("Boolean", "DEBUG", "false")
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "services"
    productFlavors {
        //a build flavor with google analytics & crashlytics dependencies
        create("gms") {
            isDefault = true
            dimension = "services"
        }
        //a build flavor free of analytics dependencies
        create("default") {
            dimension = "services"
            dependenciesInfo {
                // Disables dependency metadata when building APKs.
                includeInApk = false
                // Disables dependency metadata when building Android App Bundles.
                includeInBundle = false
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.serialization)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    /**
     * Firebase
     */

    "gmsImplementation"(platform(libs.firebase.bom))
    "gmsImplementation"(libs.firebase.crashlytics.ktx)
    "gmsImplementation"(libs.firebase.analytics.ktx)

    /**
     * Serial communication
     */
    implementation(libs.usb.serial.forandroid)

    /**
     * Sora Code Editor - https://github.com/Rosemoe/sora-editor
     */

    implementation(platform(libs.soraEditorBom))
    implementation(libs.soreEditor)
    implementation(libs.language.textmate)

    /**
     * Compose Dependencies
     */
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.test.manifest)
}
