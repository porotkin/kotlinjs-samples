import react.create
import react.dom.client.createRoot
import web.dom.ElementId
import web.dom.document

fun main() {
    createRoot(document.getElementById(ElementId("root"))!!)
        .render(Counter.create())
}
