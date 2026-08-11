rootProject.name = "web"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("../common") {
    name = "common-build"
}

dependencyResolutionManagement {
    repositories {
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

include("app")
include("common")
include("components")
