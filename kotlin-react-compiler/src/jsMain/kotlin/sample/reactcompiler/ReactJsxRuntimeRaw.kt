@file:JsModule("react/jsx-runtime")

package sample.reactcompiler

import react.ElementType
import react.Key
import react.Props
import react.ReactElement

@JsName("jsx")
external fun <P : Props> jsxRaw(
    type: ElementType<P>,
    props: P,
    key: Key = definedExternally,
): ReactElement<P>

@JsName("jsxs")
external fun <P : Props> jsxsRaw(
    type: ElementType<P>,
    props: P,
    key: Key = definedExternally,
): ReactElement<P>
