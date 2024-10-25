@file:JsModule("@cesium/widgets") // any module

package kfc.test.jso

import kotlinx.js.JsPlainObject

@JsName("Event")
external class ExternalEvent(
    type: String,
    init: ConstructorOptions? = definedExternally,
) {
    @JsPlainObject
    interface ConstructorOptions {
        val bubbles: Boolean?
        val cancelable: Boolean?
        val composed: Boolean?
    }
}
