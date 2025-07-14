import java.util.*

rootProject.name = "buildSrc"

val props = Properties().apply {
    file("../gradle.properties").inputStream().use { load(it) }
}

fun version(target: String): String {
    val value = props.getProperty("${target}.version")
    requireNotNull(value) { "$target in `.gradle.properties` is not set" }
    return value
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("kotlin", "org.jetbrains.kotlin", "kotlin-gradle-plugin").version(version("kotlin"))
            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").version(version("kotlin"))

            library(
                "serialization-json",
                "org.jetbrains.kotlinx",
                "kotlinx-serialization-json"
            ).version(version("kotlinx-serialization"))
        }
    }
}
