import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}


// apply gms & firebase plugin only for gms build flavor
if (gradle.startParameter.taskNames.any { it.contains("gms", ignoreCase = true) }) {
    pluginManager.apply(libs.plugins.google.services.get().pluginId)
    pluginManager.apply(libs.plugins.firebase.crashlytics.get().pluginId)
}

/**
 * Signing credentials, read from `local.properties` on a developer machine or
 * from environment variables on CI (where local.properties doesn't exist).
 */
private val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }
        ?.inputStream()?.use { load(it) }
}

val debugSigning = localProperties.signingCredentials("DEBUG")
val releaseSigning = localProperties.signingCredentials("RELEASE")
println("debugSigning = ${debugSigning?.storeFile?.path}")
println("releaseSigning = ${releaseSigning?.storeFile?.path}")

android {
    namespace = "micro.repl.ma7moud3ly"
    compileSdk = 37
    defaultConfig {
        applicationId = "micro.repl.ma7moud3ly"
        minSdk = 23
        targetSdk = 37
        versionCode = 16
        versionName = "3.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        debugSigning?.let { credentials ->
            getByName("debug") {
                storeFile = credentials.storeFile
                storePassword = credentials.storePassword
                keyAlias = credentials.keyAlias
                keyPassword = credentials.keyPassword
            }
        }
        releaseSigning?.let { credentials ->
            create("release") {
                storeFile = credentials.storeFile
                storePassword = credentials.storePassword
                keyAlias = credentials.keyAlias
                keyPassword = credentials.keyPassword
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("Boolean", "DEBUG", "false")
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = releaseSigning?.let { signingConfigs.getByName("release") }
        }
    }

    flavorDimensions += "services"
    productFlavors {
        //a build flavor with Google Analytics & crashlytics dependencies
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

kotlin {
    jvmToolchain(17)
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
    "gmsImplementation"(libs.firebase.crashlytics)
    "gmsImplementation"(libs.firebase.analytics)

    /**
     * Serial communication
     */
    implementation(libs.usb.serial.forandroid)

    /**
     * Nemo Code Editor - https://github.com/Ma7moud3ly/nemo-editor
     */
    implementation(libs.nemo.editor)

    /**
     * Compose Dependencies
     */
    implementation(platform(libs.androidx.compose.bom))

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



data class SigningCredentials(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String
)

/**
 * Resolves `<PREFIX>_STORE_FILE`, `_STORE_PASSWORD`, `_KEY_ALIAS` and `_KEY_PASSWORD`,
 * trying each prefix in order so one config can fall back to another.
 *
 * Returns null unless every value is present and the keystore actually exists, so
 * a partial setup never half-configures a signing config.
 */
fun Properties.signingCredentials(vararg prefixes: String): SigningCredentials? {
    fun value(key: String): String? = prefixes.firstNotNullOfOrNull { prefix ->
        val name = "${prefix}_$key"
        (this.getProperty(name) ?: System.getenv(name))?.takeIf { it.isNotBlank() }
    }
    return SigningCredentials(
        storeFile = rootProject.file(value("STORE_FILE") ?: return null)
            .takeIf { it.exists() } ?: return null,
        storePassword = value("STORE_PASSWORD") ?: return null,
        keyAlias = value("KEY_ALIAS") ?: return null,
        keyPassword = value("KEY_PASSWORD") ?: return null
    )
}
