import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode

plugins {
    alias(libs.plugins.kfc.application)
}

dependencies {
    jsMainImplementation(kotlinWrappers.browser)
}

kotlin {
    js {
        compilerOptions {
            sourceMapEmbedSources = JsSourceMapEmbedMode.SOURCE_MAP_SOURCE_CONTENT_INLINING
        }
    }
}
