plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

// apply gms & firebase plugin only for gms build flavour
if (gradle.startParameter.taskRequests.toString().contains("gms", ignoreCase = true)) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
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
        jvmToolchain(18)
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    implementation("com.google.android.material:material:1.10.0")
    implementation("com.google.code.gson:gson:2.10.1")

    /**
     * Navigation Components
     */
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.7.5")

    /**
     * Firebase
     */

    "gmsImplementation"(platform("com.google.firebase:firebase-bom:32.1.1"))
    "gmsImplementation"("com.google.firebase:firebase-crashlytics-ktx")
    "gmsImplementation"("com.google.firebase:firebase-analytics-ktx")

    /**
     * Serial communication
     */
    implementation("com.github.mik3y:usb-serial-for-android:3.5.1")

    /**
     * Sora Code Editor - https://github.com/Rosemoe/sora-editor
     */

    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.22.0"))
    implementation("io.github.Rosemoe.sora-editor:editor")
    implementation("io.github.Rosemoe.sora-editor:language-textmate")

    /**
     * Compose Dependencies
     */
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))

    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.compose.ui:ui:1.6.0-beta02")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0-beta02")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0-beta02")
    implementation("androidx.compose.material3:material3:1.2.0-alpha12")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}