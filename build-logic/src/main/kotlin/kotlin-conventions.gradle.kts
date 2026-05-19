import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
    }

    js {
        configureJsTarget(moduleName = project.name)
    }
}

fun KotlinJsTargetDsl.configureJsTarget(
    moduleName: String,
) {
    outputModuleName = moduleName

    browser {
        commonWebpackConfig {
            outputFileName = "index.js"

            cssSupport {
                enabled = true
            }
        }
    }

    binaries.executable()

    compilerOptions {
        target = "es2015"

        sourceMap.set(true)
        sourceMapEmbedSources.set(JsSourceMapEmbedMode.SOURCE_MAP_SOURCE_CONTENT_ALWAYS)
    }
}
