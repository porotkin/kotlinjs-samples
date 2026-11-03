package sample.reactcompiler

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useEffect
import seskar.js.JsValue

sealed external interface BadgeTone {
    companion object {
        @JsValue("NEUTRAL")
        val NEUTRAL: BadgeTone

        @JsValue("ACCENT")
        val ACCENT: BadgeTone
    }
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
        +"[${props.tone}] ${DefaultConfig.PREFIX}: ${props.text}"
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
 * - **Mitigation:**
 *   1. Use Seskar `@JsValue` unions for [BadgeTone] so `BadgeTone.ACCENT` lowers to the string literal `'ACCENT'`;
 *      the IR plugin marks component vals `@EagerInitialization`, so same-file reads skip `get_ChildBadge()`.
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
