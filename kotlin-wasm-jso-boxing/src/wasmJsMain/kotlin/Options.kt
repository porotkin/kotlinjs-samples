@file:OptIn(ExperimentalWasmJsInterop::class)

external interface Options : JsAny {
    var value: Int
}

fun createOptions(): Options =
    js("({})")

fun withApply(): Options =
    createOptions().apply { value = 1 }

fun withAlso(): Options =
    createOptions().also { it.value = 1 }

fun withTakeIf(): Options? =
    createOptions().takeIf { it.value == 0 }

@Suppress("SimpleRedundantLet")
fun withLet(): Int =
    createOptions().let { it.value }

fun withRun(): Int =
    createOptions().run { value }

fun withWith(): Int =
    with(createOptions()) { value }

fun withoutScopeFunction(): Options {
    val options = createOptions()
    options.value = 1
    return options
}

fun discarded() {
    createOptions()
}
