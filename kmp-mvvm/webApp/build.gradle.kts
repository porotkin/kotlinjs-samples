plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js {
        outputModuleName = "webApp"
        browser()
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project.dependencies.platform(libs.wrappers.bom))
            implementation(libs.wrappers.react)
            implementation(libs.wrappers.reactDom)
        }
    }
}
