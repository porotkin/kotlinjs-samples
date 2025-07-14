import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode

plugins {
    alias(kfc.plugins.application)

    `fix-source-maps`
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
