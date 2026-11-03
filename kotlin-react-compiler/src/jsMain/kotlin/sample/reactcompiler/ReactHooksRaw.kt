@file:JsModule("react")

package sample.reactcompiler

import js.array.ReadonlyArray

@JsName("useEffect")
external fun useEffectRawInline(
    effect: () -> Unit,
    dependencies: ReadonlyArray<Any?> = definedExternally,
)
