rootProject.name = "web"

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

include("app")
include("common")
include("components")
