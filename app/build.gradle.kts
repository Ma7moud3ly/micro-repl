plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
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
        versionCode = 9
        versionName = "1.4"
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
        }
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.composeCompiler.get().version
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.material)
    implementation(libs.gson)

    /**
     * Navigation Components
     */
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

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
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.test.manifest)
}
