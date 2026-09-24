import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // rewrites the `modules(AppModule::class)` call in main
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
        }
    }
}