@file:OptIn(InternalApi::class)

package sample.reactcompiler

import js.internal.InternalApi
import js.objects.Object
import js.objects.unsafeJso
import js.reflect.Reflect
import js.reflect.unsafeCast
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.jsx.runtime.jsx

@PublishedApi
internal fun buildRootChildren(
    builder: FragmentProps,
): ReactNode? {
    val direct = builder.children
    if (direct != null) return direct
    val symbols = Object.getOwnPropertySymbols(builder)
    return if (symbols.isNotEmpty()) {
        unsafeCast<ReactNode>(Reflect.get(builder, symbols[0]))
    } else {
        null
    }
}

/**
 * Case 11: Kotlin Wrappers Mitigation Demo
 *
 * Demonstrates how changing `FC` (and `useMemo` / `useCallback`) in `kotlin-react` wrappers
 * mitigates Cases 02, 03, 04, and 07 without a custom Kotlin IR compiler plugin:
 *
 * 1. Emits `"use memo"` at the top of the component function so React Compiler discovers both
 *    stateful and stateless components in `annotation` or `infer` mode (Case 07).
 * 2. Inlines root `ChildrenBuilder` creation and `jsx(Fragment, builder)` directly into the
 *    component function instead of wrapping the body inside `Fragment.create { ... }`, keeping
 *    all hook calls at the top level of the component function (Case 02).
 * 3. Makes `useMemoInline` / `useCallbackInline` direct inline pass-throughs, avoiding reversed-parameter
 *    spilled temporaries (Case 03) and Seskar `ValueTransformer` dependency array rewrites (Case 04).
 */
@OptIn(InternalApi::class)
inline fun <P : Props> compilerFriendlyFC(
    crossinline block: @ReactDsl ChildrenBuilder.(props: P) -> Unit,
): FC<P> =
    unsafeCast(
        provider = { props: P ->
            js("\"use memo\"")
            val builder: FragmentProps = unsafeJso()
            block(unsafeCast(builder), props)
            val children = buildRootChildren(builder)
            if (children != null) {
                jsx(Fragment, builder)
            } else {
                unsafeCast<ReactElement<*>>(null)
            }
        },
    )

inline fun <T> useMemoInline(
    vararg dependencies: Any?,
    crossinline callback: () -> T,
): T {
    js("void dependencies")
    return callback()
}

inline fun <T : Function<*>> useCallbackInline(
    vararg dependencies: Any?,
    callback: T,
): T {
    js("void dependencies")
    return callback
}

external interface CompilerFriendlyCounterProps : Props {
    var title: String
    var step: Int
}

val CompilerFriendlyCounter: FC<CompilerFriendlyCounterProps> = compilerFriendlyFC { props ->
    val (count, setCount) = useState(0)
    val label = useMemoInline(props.title, count) {
        "${props.title}: $count"
    }
    val onIncrement = useCallbackInline(props.step) {
        setCount { it + props.step }
    }

    div {
        span { +label }
        button {
            onClick = { onIncrement() }
            +"Increment"
        }
    }
}
