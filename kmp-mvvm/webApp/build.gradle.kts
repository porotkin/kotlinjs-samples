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
            implementation(projects.common)
            implementation(project.dependencies.platform(libs.wrappers.bom))
            implementation(libs.wrappers.react)
            implementation(libs.wrappers.reactDom)
            implementation(libs.wrappers.reactUse)
            implementation(libs.wrappers.muiMaterial)
            implementation(libs.wrappers.emotionReact)
            implementation(libs.wrappers.emotionStyled)
        }
    }
}
