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

            library("kotlin-testJs", "org.jetbrains.kotlin", "kotlin-test-js").version(kotlinVersion)

            val coroutinesVersion = extra["kotlinx-coroutines.version"] as String
            library("coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version(coroutinesVersion)
            library("coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").version(coroutinesVersion)

            val ktorVersion = extra["ktor.version"] as String
            plugin("ktor", "io.ktor.plugin").version(ktorVersion)
            library("server-core", "io.ktor", "ktor-server-core-jvm").version(ktorVersion)
            library("server-netty", "io.ktor", "ktor-server-netty-jvm").version(ktorVersion)
            library("server-html-builder", "io.ktor", "ktor-server-html-builder").version(ktorVersion)

            val logbackVersion = extra["logback.version"] as String
            library("logback", "ch.qos.logback", "logback-classic").version(logbackVersion)
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

include("webpack-per-file-issue")