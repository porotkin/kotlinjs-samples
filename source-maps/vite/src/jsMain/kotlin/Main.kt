import web.dom.document

fun main() {
    val view = createView()

    document.appendChild(
        node = view.withStyling(),
    )
}
