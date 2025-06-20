import web.dom.document
import web.html.HTML.div
import web.html.HTMLDivElement

typealias View = HTMLDivElement

fun createView(): View =
    document.createElement(div)
