plugins {
    alias(libs.plugins.kfc.application)
    alias(libs.plugins.seskar)
    alias(libs.plugins.kotlin.jsPlainObjects)
}

dependencies {
    jsMainImplementation(libs.coroutines.core)
    jsMainImplementation(kotlinWrappers.js)
    jsMainImplementation(kotlinWrappers.react)
    jsMainImplementation(kotlinWrappers.reactDom)
}
