import react.FC
import react.dom.html.ReactHTML.button
import react.useState

val Counter = FC {
    var count by useState(0)
    button {
        onClick = { count += 1 }
        +"Count: $count"
    }
}
