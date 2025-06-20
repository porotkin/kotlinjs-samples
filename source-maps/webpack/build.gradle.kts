plugins {
    `kotlin-tools-conventions`
    `kotlin-conventions`
}

dependencies {
    jsMainImplementation(kotlinWrappers.browser)
}

kotlin {
    js {
        outputModuleName = "source-maps-webpack"
    }
}

extensions.extraProperties.set("kotlin.js.ir.output.granularity", "per-file")
