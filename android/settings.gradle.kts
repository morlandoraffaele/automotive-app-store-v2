pluginManagement {
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

rootProject.name = "automotive-app-store"

// Copied verbatim from radioplayer-automotive-radio/core/designsystem, with the
// product flavors and the `radio.*` convention plugins replaced by plain AGP config.
include(":core:designsystem")
include(":core:radio-image")
include(":app")
