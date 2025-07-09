import web.dom.clickEvent
import web.dom.document
import web.events.addHandler
import web.html.HTMLDivElement
import web.html.HtmlTagName.button
import web.html.HtmlTagName.div

typealias View = HTMLDivElement

fun createView(): View {
    val view = document.createElement(div)
    val button = document.createElement(button)

    button.style.width = "250px"
    button.style.height = "100px"

    button.textContent = "Click me!"

    button.clickEvent.addHandler {
        error("Clicked!")
    }

    view.appendChild(button)

    return view
}
