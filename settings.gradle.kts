@file:Suppress("UnstableApiUsage")

include(":feature:notifications")


include(":feature:config")


pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Galixo Clicker"
include(":app")

include(":feature:backup")
include(":core:common:display")
include(":core:common:base")
include(":core:common:overlays")
include(":core:common:ui")
include(":core:common:permissions")