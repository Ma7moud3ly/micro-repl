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
    application {
        mainClass = "${libs.versions.project.packageName.get()}.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = libs.versions.project.packageName.get()
            packageVersion = libs.versions.project.versionName.get()

            vendor = libs.versions.project.vendor.get()

            // jpackage takes a different image format on each platform
            val icons = rootProject.file("shared/src/commonMain/composeResources/drawable")
            windows {
                iconFile.set(icons.resolve("icon.ico"))
                shortcut = true
            }
            linux {
                iconFile.set(icons.resolve("logo.png"))
                shortcut = true
            }
            macOS {
                iconFile.set(icons.resolve("icon.icns"))
            }
        }
    }
}
