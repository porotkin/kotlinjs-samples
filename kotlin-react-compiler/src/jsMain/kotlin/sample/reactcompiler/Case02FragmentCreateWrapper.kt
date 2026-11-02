package sample.reactcompiler

import js.reflect.unsafeCast
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span

external interface CounterCardProps : Props {
    var title: String
    var step: Int
}

/**
 * Case 02a: Upstream `kotlin-react` Non-Inline `FC` (`CounterCard$lambda($this$FC, props)`)
 *
 * - **Cause:** In upstream `kotlin-react`, `fun <P : Props> FC(block)` (`kotlin-react/react/FC.mjs`) is
 *   **not `inline`**. The actual component function `(props) => createElementOrNull(...)` lives inside
 *   `kotlin-react/react/FC.mjs`, while the user module emits `function CounterCard$lambda($this$FC, props)`
 *   passed to `memo(FC(CounterCard$lambda))`.
 * - **React Compiler Behavior:**
 *   1. `CounterCard$lambda($this$FC, props)` takes two parameters `($this$FC, props)`, mutates its first
 *      parameter `$this$FC` (`setDefaultKey($this$FC, ...)`, `addChild($this$FC, ...)`), and returns `Unit`.
 *   2. When React Compiler compiles `CounterCard$lambda`, it treats the first parameter `$this$FC` as frozen
 *      component `props` and fails validation with:
 *      `Error: This value cannot be modified. Modifying component props or hook arguments is not allowed.`
 *   3. Even if parameter mutation were ignored, `PruneNonEscapingScopes` prunes all reactive scopes because
 *      `$this$FC` is a parameter rather than the return value (`return Unit_getInstance()`).
 */
val CounterCard: FC<CounterCardProps> = FC { props ->
    val (count, setCount) = useState(0)

    div {
        span { +"${props.title}: $count" }
        button {
            onClick = { setCount { it + props.step } }
            +"Increment"
        }
    }
}

inline fun inlineFragmentCreateElementOrNull(
    crossinline block: @ReactDsl ChildrenBuilder.() -> Unit,
): ReactElement<*>? =
    Fragment.create { block() }
        .takeIf { it.props.children != null }

inline fun <P : Props> inlineFragmentFC(
    crossinline block: @ReactDsl ChildrenBuilder.(props: P) -> Unit,
): FC<P> =
    unsafeCast(
        provider = { props: P ->
            inlineFragmentCreateElementOrNull { block(props) }
        },
    )

inline fun inlineFragmentFC(
    crossinline block: @ReactDsl ChildrenBuilder.() -> Unit,
): FC<Props> =
    unsafeCast(
        provider = { ->
            inlineFragmentCreateElementOrNull(block)
        },
    )

/**
 * Case 02b: Inlining `FC` on Top of `Fragment.create` (1-arg `FC<Props>`)
 *
 * - **Cause:** If `FC` and `createElementOrNull` are made `inline` on top of `Fragment.create { block(props) }`,
 *   `ElementType.create` (`kotlin-react/react/create.mjs`) is still not `inline`, so the component body is
 *   compiled inside a nested arrow function `create(Fragment, ($this$create) => { ... })`.
 * - **React Compiler Behavior:** Calling `useState` inside the nested arrow function fails validation with:
 *   `Error: Hooks must be called at the top level in the body of a function component or custom hook, and may not be called within function expressions. Cannot call hook within a function expression.`
 */
val InlineFragmentCounter: FC<CounterCardProps> = inlineFragmentFC { props ->
    val (count, setCount) = useState(0)

    div {
        span { +"${props.title}: $count" }
        button {
            onClick = { setCount { it + props.step } }
            +"Increment"
        }
    }
}

/**
 * Case 02c: Inlining `FC` on Top of `Fragment.create` (0-arg `FC` with Hoisted `$lambda$lambda`)
 *
 * - **Cause:** A 0-arg `inlineFragmentFC { ... }` has a non-capturing builder lambda, so Kotlin/JS hoists
 *   the component body into `ZeroArgCounter$lambda$lambda($this$create)` while `ZeroArgCounter$lambda()`
 *   only calls `create(Fragment, ZeroArgCounter$lambda$lambda)`.
 * - **React Compiler Behavior:**
 *   1. `ZeroArgCounter$lambda()` has no hooks and no JSX AST nodes, so it is skipped.
 *   2. `ZeroArgCounter$lambda$lambda($this$create)` is misidentified as the component with `$this$create`
 *      treated as frozen `props`, failing on `$this$create` mutation and pruning all scopes.
 * - **Fix:** Inline root `ChildrenBuilder` creation and `jsx(Fragment, builder)` directly inside `FC`
 *   in `kotlin-react` (see [Case11WrapperMitigationDemo]).
 */
val ZeroArgCounter: FC<Props> = inlineFragmentFC {
    val (count, setCount) = useState(0)

    div {
        span { +"Count: $count" }
        button {
            onClick = { setCount { it + 1 } }
            +"+1"
        }
    }
}
