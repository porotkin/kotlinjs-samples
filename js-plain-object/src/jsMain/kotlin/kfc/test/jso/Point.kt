package kfc.test.jso

import kotlinx.js.JsPlainObject

external class Point(
    options: ConstructorOptions,
) {
    val x: Double
    val y: Double

    @JsPlainObject
    interface ConstructorOptions {
        val x: Double
        val y: Double
    }
}
