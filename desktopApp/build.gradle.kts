import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.koinCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}

compose.desktop {
    val projectName = libs.versions.project.name.get()
    val packageName = libs.versions.project.packageName.get()
    application {
        mainClass = "$packageName.MainKt"

        buildTypes.release.proguard {
            isEnabled.set(false)
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            // the whole Java runtime, so no module a library needs is left out
            includeAllModules = true
            // the name in the start menu and the window list
            this.packageName = projectName
            packageVersion = libs.versions.project.versionName.get()
            description = libs.versions.project.description.get()
            vendor = libs.versions.project.vendor.get()
            // jpackage takes a different image format on each platform
            val icons = rootProject.file("shared/src/commonMain/composeResources/drawable")
            windows {
                iconFile.set(icons.resolve("icon.ico"))
                shortcut = true
                menuGroup = projectName
                // the same on every release, so a new installer replaces the old version
                upgradeUuid = "ce9f0dd3-25a4-4ef7-8523-c8c83a03f999"
            }
            linux {
                iconFile.set(icons.resolve("logo.png"))
                shortcut = true
                this.packageName = projectName.lowercase().replace(' ', '-')
                menuGroup = "Development"
                appCategory = "Development"
            }
            macOS {
                iconFile.set(icons.resolve("icon.icns"))
                bundleID = packageName
            }
        }
    }
}
