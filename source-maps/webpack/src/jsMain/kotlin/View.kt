import web.dom.document
import web.html.HTMLDivElement
import web.html.HtmlTagName.div

typealias View = HTMLDivElement

fun createView(): View =
    document.createElement(div)
