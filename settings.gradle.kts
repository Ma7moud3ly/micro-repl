pluginManagement {
    repositories {
        maven(url = "https://jitpack.io")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://jitpack.io")
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Micro REPL"

// the shipped Android app, until the shared module takes over
include(":app")

// the multiplatform build
include(":shared")
include(":androidApp")
include(":desktopApp")
include(":webApp")
