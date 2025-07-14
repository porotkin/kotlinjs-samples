fun View.withStyling(): View {
    with(style) {
        display = "flex"
        flexDirection = "column"
        alignItems = "center"

        backgroundColor = "red"

        width = "100%"
        height = "50vh"
    }

    return this
}
