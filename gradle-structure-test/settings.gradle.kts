rootProject.name = "gradle-structure-test"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

includeBuild("common")
includeBuild("mobile")
includeBuild("web")
includeBuild("server")
