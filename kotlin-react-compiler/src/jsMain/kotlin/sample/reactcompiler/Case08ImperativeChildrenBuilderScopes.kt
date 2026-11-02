package sample.reactcompiler

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header
import react.dom.html.ReactHTML.section
import react.dom.html.ReactHTML.span
import react.useState

external interface ExpandableSectionProps : Props {
    var visible: Boolean
    var title: String
    var subtitle: String
}

/**
 * Case 08: Imperative `ChildrenBuilder` (`$this$create`, `addChild`, `setDefaultKey`) vs. `JSXElement` AST
 *
 * - **React Compiler Behavior:**
 *   1. **Scope Flattening Across Hooks (`FlattenScopesWithHooksOrUseHIR`):**
 *      Even when all hooks (`useState`) are placed at the very top of the `FC` lambda, `val builder = unsafeJso()`
 *      in `FC` is allocated before `block(builder, props)` (and therefore before `useState`) and mutated by
 *      `addChild(builder, ...)` after `useState`. Because `builder`'s mutable range spans across a hook call,
 *      `FlattenScopesWithHooksOrUseHIR` destroys any reactive scope spanning that hook call, leaving the root
 *      `jsx(Fragment, builder)` unmemoized.
 *   2. **Early `return@FC` Lowered to `$l$block: { ... break $l$block; }`:**
 *      Because `FC` is an inline function wrapping `block(builder, props)`, `if (!props.visible) return@compilerFriendlyFC`
 *      (placed after all hooks) compiles to `break $l$block` rather than `return null`, keeping `builder` alive
 *      across the entire block.
 *   3. **Coarse/Merged Scopes & Nested Builder Closures:**
 *      Even when `builder = {}` is moved after all hooks, `addChild(builder, 'div', ($this$div) => { ... })`
 *      mutates `builder` and wraps child trees inside nested closures (`header { ... }`, `section { ... }`).
 *      React Compiler does not create reactive scopes inside nested closures and merges all mutations to
 *      `builder` into a single coarse scope (whereas `JSXElement` AST nodes lower to `JSX` instructions
 *      with `Create = frozen` and `ImmutableCapture`, allowing independent memoization of `header` vs. `section`).
 */
val ExpandableSection: FC<ExpandableSectionProps> = compilerFriendlyFC { props ->
    val (expanded, setExpanded) = useState(false)

    if (!props.visible) return@compilerFriendlyFC

    div {
        header {
            span { +props.title }
            button {
                onClick = { setExpanded { !it } }
                +if (expanded) "Collapse" else "Expand"
            }
        }
        if (expanded) {
            section {
                span { +props.subtitle }
            }
        }
    }
}
