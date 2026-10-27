plugins {
    kotlin("multiplatform")
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        binaries.executable()

        compilerOptions {
            freeCompilerArgs.add("-Xwasm-generate-wat")
        }
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(kotlinWrappers.js)
        }
    }
}
