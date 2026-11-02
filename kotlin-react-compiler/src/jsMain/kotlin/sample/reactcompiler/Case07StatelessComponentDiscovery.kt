package sample.reactcompiler

import js.internal.InternalApi
import js.objects.unsafeJso
import js.reflect.unsafeCast
import react.*
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.strong
import react.jsx.runtime.jsx

external interface StatusBadgeProps : Props {
    var label: String
    var count: Int
}

@OptIn(InternalApi::class)
inline fun <P : Props> inlineStatelessFCWithoutDirective(
    crossinline block: @ReactDsl ChildrenBuilder.(props: P) -> Unit,
): FC<P> =
    unsafeCast(
        provider = { props: P ->
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

/**
 * Case 07: Component Discovery Failure in Default (`infer`) Mode & Missing `TObject<BuiltInProps>` Type
 *
 * - **Cause:** Kotlin/JS names component functions `StatusBadge$lambda` and emits `jsx()` / `addChild()`
 *   function calls rather than `JSXElement` / `JSXFragment` AST nodes.
 * - **React Compiler Behavior:**
 *   1. In default `infer` mode, React Compiler only compiles a PascalCase function if its body directly
 *      contains `JSXElement` / `JSXFragment` AST nodes or top-level hook calls (`use*`). Even after
 *      `Fragment.create` is inlined (as in [StatusBadge] below), every stateless component has no hook
 *      calls and no JSX AST nodes, so React Compiler **silently skips all of them** unless `"use memo"` is present.
 *   2. Even with `"use memo"` (as in [AnnotatedStatusBadge]), a function without `JSXElement` AST nodes and
 *      without hook calls is typed by `InferTypes` as `AnnotatedStatusBadge(<unknown> props)` instead of
 *      `AnnotatedStatusBadge(props: TObject<BuiltInProps>)`, losing the guarantee that `props` is frozen/immutable.
 * - **Fix:** Emit `"use memo"` in the `FC` wrapper (see [Case11WrapperMitigationDemo]).
 */
val StatusBadge: FC<StatusBadgeProps> = inlineStatelessFCWithoutDirective { props ->
    span {
        strong { +props.label }
        +" (${props.count})"
    }
}

val AnnotatedStatusBadge: FC<StatusBadgeProps> = compilerFriendlyFC { props ->
    span {
        strong { +props.label }
        +" (${props.count})"
    }
}
