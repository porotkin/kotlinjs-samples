package kfc.test.jso

import cesium.widgets.Viewer
import web.html.HTMLElement

fun CesiumViewer(
    container: HTMLElement,
): Viewer {
    val options = Viewer.ConstructorOptions(
        animation = false,
        baseLayerPicker = false,
        geocoder = false,
        homeButton = false,
        infoBox = false,
        sceneModePicker = false,
        selectionIndicator = false,
        timeline = false,
        navigationHelpButton = true,
        navigationInstructionsInitiallyVisible = false,
        fullscreenButton = false,
        requestRenderMode = true,
        maximumRenderTimeChange = Double.POSITIVE_INFINITY,
        scene3DOnly = true,
    )

    return Viewer(container, options)
}
