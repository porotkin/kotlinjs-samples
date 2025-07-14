import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import web.cssom.px

internal val View: FC<Props> = FC {
    div {
        button {
            css {
                width = 250.px
                height = 100.px
            }

            onClick = { error("Clicked!") }

            +"Click me!"
        }
    }
}
