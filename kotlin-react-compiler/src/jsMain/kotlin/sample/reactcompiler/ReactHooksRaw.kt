@file:JsModule("react")

package sample.reactcompiler

import js.array.ReadonlyArray
import js.array.Tuple2

@JsName("useTransition")
external fun useTransitionInline(): Tuple2<Boolean, (() -> Unit) -> Unit>

@JsName("useEffect")
external fun useEffectRawInline(
    effect: () -> Unit,
    dependencies: ReadonlyArray<Any?> = definedExternally,
)
