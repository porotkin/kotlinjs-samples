import web.dom.document

fun main() {
    val view = createView()

    document.body.appendChild(
        node = view.withStyling(),
    )
}
