plugins {
    alias(kfc.plugins.library)
}

dependencies {
    jsTestImplementation(libs.kotlin.testJs)
    jsTestImplementation(libs.coroutines.test)

    jsMainImplementation(kotlinWrappers.cesium.engine)
    jsMainImplementation(kotlinWrappers.cesium.widgets)
}
