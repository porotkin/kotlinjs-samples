rootProject.name = "mobile"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("../common") {
    name = "common-build"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("commonProjects") {
            from(files("../common/gradle/projects.versions.toml"))
        }
    }
}

include("android-app")
include("compose-app")
include("ios-umbrella")
include("shared")
