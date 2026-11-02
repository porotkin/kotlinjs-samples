package sample.reactcompiler

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.footer
import react.dom.html.ReactHTML.h3
import web.html.ButtonType
import web.html.button

external interface ConfirmBannerProps : Props {
    var message: String
}

/**
 * Case 06: Mutating `$this$...` Receiver Parameters in Hoisted `ChildrenBuilder` / `Props` Lambdas
 *
 * - **Cause:** Non-capturing child `Props` builder blocks inside a component (such as the `footer` and
 *   `button` blocks below that do not capture `props`) are hoisted by Kotlin/JS to top-level functions
 *   named `ConfirmBanner$lambda$lambda$lambda($this$footer)` / `...($this$button)` that mutate properties
 *   on their first parameter (`$this$button.type = 'button'`).
 * - **React Compiler Behavior:** If compiled by React Compiler (e.g., in `compilationMode: "all"` or if
 *   matched as a component), React Compiler treats the first parameter `$this$button` as frozen component
 *   `props` and fails with:
 *   `Error: This value cannot be modified. Modifying component props or hook arguments is not allowed.`
 * - **Fix:** Run React Compiler in `compilationMode: "annotation"` and emit `"use memo"` only on actual
 *   `FC` component functions and top-level `use*` hooks, never on hoisted `$this$...` builder helpers.
 */
val ConfirmBanner: FC<ConfirmBannerProps> = FC { props ->
    div {
        h3 { +props.message }
        footer {
            button {
                type = ButtonType.button
                +"Proceed"
            }
        }
    }
}
