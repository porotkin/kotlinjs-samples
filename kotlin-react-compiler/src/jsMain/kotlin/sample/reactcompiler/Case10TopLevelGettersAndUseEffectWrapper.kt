package sample.reactcompiler

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useEffect

enum class BadgeTone {
    NEUTRAL,
    ACCENT,
}

object DefaultConfig {
    const val PREFIX: String = "Item"
}

external interface ChildBadgeProps : Props {
    var text: String
    var tone: BadgeTone
}

val ChildBadge: FC<ChildBadgeProps> = FC { props ->
    span {
        +"[${props.tone.name}] ${DefaultConfig.PREFIX}: ${props.text}"
    }
}

external interface EffectAndGettersDemoProps : Props {
    var label: String
}

/**
 * Case 10: Top-Level Property/Singleton/Enum Getters (`get_Foo()`, `Foo_getInstance()`) & `kotlin-react` `useEffect` Wrapper
 *
 * - **Cause:**
 *   1. Component references (`get_ChildBadge()`), `object` singletons (`DefaultConfig_getInstance()`), and
 *      `enum` entries (`BadgeTone_ACCENT_getInstance()`) are emitted as 0-arg function calls rather than
 *      module-level constant identifiers.
 *   2. `kotlin-react`'s `useEffect(dependencies, effect)` (`kotlin-react/react/useEffect.mjs`) reverses the
 *      argument order relative to React's `useEffect(effect, dependencies)` and wraps the coroutine `slambda`.
 * - **React Compiler Behavior:**
 *   1. React Compiler treats each 0-arg getter call (`get_ChildBadge()`, `BadgeTone_ACCENT_getInstance()`)
 *      as a potentially allocating function call and reserves a `Symbol.for("react.memo_cache_sentinel")`
 *      slot in `_c(N)` for every getter call.
 *   2. React Compiler memoizes `[props.label]` and the `slambda` passed to `kotlin-react`'s `useEffect` wrapper.
 */
val EffectAndGettersDemo: FC<EffectAndGettersDemoProps> = FC { props ->
    useEffect(props.label) {
        console.log("Rendered label:", props.label)
    }

    div {
        ChildBadge {
            text = props.label
            tone = BadgeTone.ACCENT
        }
    }
}
