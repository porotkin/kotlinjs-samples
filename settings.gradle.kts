import org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder

rootProject.name = "kotlinjs-samples"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

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

    versionCatalogs {
        register("kotlinWrappers") {
            val kotlinWrappersCatalog = named("libs")
                .map { it as DefaultVersionCatalogBuilder }
                .map { it.build() }
                .map { it.getDependencyData("catalogs.kotlinWrappers") }
                .map { "${it.group}:${it.name}:${it.version}" }
                .get()

            from(kotlinWrappersCatalog)
        }
    }
}

includeBuild("build-logic")
includeBuild("gradle-structure-test/composite-builds/common")
includeBuild("gradle-structure-test/composite-builds")
includeBuild("gradle-structure-test/isolated-projects")
includeBuild("kmp-mvvm")

include("jvm-incremental-compilation")
include("jvm-incremental-compilation:common")
include("jvm-incremental-compilation:server")
include("jvm-object-not-defined:server")
include("kotlin-extra-import-bug")
include("kotlin-unused-import-bug")
include("kotlin-wasm-when-bug")
include("kotlin-wasm-jso-boxing")
