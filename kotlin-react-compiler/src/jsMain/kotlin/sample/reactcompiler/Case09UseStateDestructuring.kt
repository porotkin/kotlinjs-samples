package sample.reactcompiler

import js.array.component1
import js.array.component2
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useState

/**
 * Case 09: `useState` & `useTransition` Destructuring via Indexed Access (`_destruct[0]`, `_destruct.at(0)`)
 *
 * - **Cause:** In Kotlin/JS, `val (count, setCount) = useState(0)` lowers to `_destruct[0]` and `_destruct[1]`,
 *   and `val (isPending, startTransition) = useTransition()` lowers to `_destruct.at(0)` and `_destruct.at(1)`.
 * - **Mitigation:** Bind [useTransitionInline] directly to `'react'` and rewrite
 *   `_destruct[0]` / `_destruct[1]` and `_destruct.at(0)` / `_destruct.at(1)` into JS `ArrayPattern`
 *   destructuring (`const [state, setState] = ...`) in the Vite pre-transform plugin so `InferTypes`
 *   assigns the `SetState` and `StartTransition` built-in types.
 */
val DestructuredStateDemo: FC<Props> = FC {
    val (count, setCount) = useState(0)
    val (isPending, startTransition) = useTransitionInline()

    div {
        span { +"Count: $count (pending: $isPending)" }
        button {
            onClick = {
                startTransition {
                    setCount { it + 1 }
                }
            }
            +"Increment in Transition"
        }
    }
}
