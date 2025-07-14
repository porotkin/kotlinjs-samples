rootProject.name = "kotlinjs-samples"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val kotlinVersion = extra["kotlin.version"] as String
            plugin("kotlin-multiplatform", "org.jetbrains.kotlin.multiplatform").version(kotlinVersion)
            plugin("kotlin-jsPlainObjects", "org.jetbrains.kotlin.plugin.js-plain-objects").version(kotlinVersion)

            val seskarVersion = extra["seskar.version"] as String
            plugin("seskar", "io.github.turansky.seskar").version(seskarVersion)

            library("kotlin-testJs", "org.jetbrains.kotlin", "kotlin-test-js").version(kotlinVersion)
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").version(kotlinVersion)
            library("kotlin-test-junit", "org.jetbrains.kotlin", "kotlin-test-junit").version(kotlinVersion)

            val coroutinesVersion = extra["kotlinx-coroutines.version"] as String
            library("coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version(coroutinesVersion)
            library("coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").version(coroutinesVersion)
        }

        create("kfc") {
            val kfcVersion = extra["kfc-plugins.version"] as String
            plugin("application", "io.github.turansky.kfc.application").version(kfcVersion)
            plugin("library", "io.github.turansky.kfc.library").version(kfcVersion)
            plugin("worker", "io.github.turansky.kfc.worker").version(kfcVersion)
        }

        create("kotlinWrappers") {
            val wrappersVersion = extra["kotlin-wrappers.version"] as String
            from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:$wrappersVersion")
        }
    }
}

include("js-plain-object")
include("jvm-incremental-compilation")
include("jvm-incremental-compilation:common")
include("jvm-incremental-compilation:server")
include("jvm-object-not-defined:server")
include("source-maps:vite")
include("source-maps:webpack")
