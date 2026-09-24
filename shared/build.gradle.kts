import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.koinCompiler)
}

val javaVersion = libs.versions.java.version.get()
val appPackage = libs.versions.project.packageName.get()
val appVersionName = libs.versions.project.versionName.get()
val appVersionCode = libs.versions.project.versionCode.get()

/** True unless this looks like a release build - see [generateBuildInfo]. */
val isReleaseBuild = gradle.startParameter.taskNames.any {
    it.contains("Release", ignoreCase = true) || it.contains("bundle", ignoreCase = true)
}

val generateBuildInfo = tasks.register<GenerateBuildInfo>("generateBuildInfo") {
    group = "build"
    description = "Writes BuildInfo.kt, the build facts common code needs."
    packageName.set(appPackage)
    debug.set(!isReleaseBuild)
    versionName.set(appVersionName)
    versionCode.set(appVersionCode)
    outputDir.set(layout.buildDirectory.dir("generated/buildInfo/kotlin"))
}

kotlin {
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    android {
        namespace = "$appPackage.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaVersion)
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonMain.configure { kotlin.srcDir(generateBuildInfo.map { it.outputDir }) }

        commonMain.dependencies {
            // compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            api(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // navigation
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.navigation3.ui)
            implementation(libs.lifecycle.viewmodel.navigation3)

            // koin - the bom keeps the artifacts below on one version
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.annotations)

            // kotlinx
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.json)

            // the code editor
            implementation(libs.nemo.editor)

            // the file picker
            implementation(libs.filekit.dialogs.compose)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)

            // BackHandler, and the Activity the AppManager drives
            implementation(libs.androidx.activity.compose)
            // FileProvider, for sharing a script
            implementation(libs.androidx.core.ktx)
            // the USB serial transport
            implementation(libs.usb.serial.forandroid)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    packageOfResClass = "$appPackage.shared.resources"
    generateResClass = auto
    publicResClass = true
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}


/**
 * Generates `BuildInfo.kt` into a source set every target compiles, holding
 * whether this is a debug build and the version it was built at.
 *
 * Reruns whenever those values change.
 */
abstract class GenerateBuildInfo : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val debug: Property<Boolean>

    @get:Input
    abstract val versionName: Property<String>

    @get:Input
    abstract val versionCode: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val pkg = packageName.get()
        val file = outputDir.get().asFile
            .resolve(pkg.replace('.', '/'))
            .resolve("BuildInfo.kt")
        file.parentFile.mkdirs()
        file.writeText(
            """
            package $pkg

            /** Generated by the `generateBuildInfo` task - do not edit. */
            object BuildInfo {
                const val DEBUG: Boolean = ${debug.get()}
                const val VERSION_NAME: String = "${versionName.get()}"
                const val VERSION_CODE: Int = ${versionCode.get()}
            }
            """.trimIndent() + "\n"
        )
    }
}

