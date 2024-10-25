package kfc.test.jso

fun ExternalEvent(
    type: String,
): ExternalEvent {
    val options = ExternalEvent.ConstructorOptions(
        bubbles = true,
        cancelable = false,
        composed = true,
    )

    return ExternalEvent(
        type,
        options,
    )
}
