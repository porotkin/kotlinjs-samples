rootProject.name = "isolated-projects"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":common:client")
include(":common:core")
include(":common:webapi")

include(":web:app")
include(":web:test-common")
include(":web:components")

include(":server:app")
include(":server:core")
include(":server:database")

include(":mobile:android-app")
include(":mobile:compose-app")
include(":mobile:ios-umbrella")
include(":mobile:shared")
