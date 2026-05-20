rootProject.name = "mobile"

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
        maven {
            url = uri("../build/local-maven")
        }
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("commonProjects") {
            from("dev.gradle-structure-test.common.catalog:projects:1.0.0")
        }
    }
}

include("android-app")
include("compose-app")
include("ios-umbrella")
include("shared")
