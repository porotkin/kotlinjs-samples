plugins {
    alias(kfc.plugins.library)
}

dependencies {
    jsTestImplementation(libs.kotlin.testJs)

    jsMainImplementation(kotlinWrappers.cesium.engine)
    jsMainImplementation(kotlinWrappers.cesium.widgets)
}
