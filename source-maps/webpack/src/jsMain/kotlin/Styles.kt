fun View.withStyling(): View {
    with(style) {
        display = "flex"
        flexDirection = "column"
        alignItems = "center"
    }

    return this
}
