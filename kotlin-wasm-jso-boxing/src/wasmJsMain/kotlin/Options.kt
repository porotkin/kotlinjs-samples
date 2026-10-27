import js.objects.unsafeJso

@OptIn(ExperimentalWasmJsInterop::class)
external interface Options : JsAny {
    var value: Int
}

// `unsafeJso<T>().apply(block)`: boxed via `jsToKotlinAnyAdapter`, unboxed twice via `kotlinToJsAnyAdapter`
fun optionsWithApply(): Options =
    unsafeJso { value = 1 }

// Same without `apply`: stays `externref`
fun optionsWithoutApply(): Options {
    val options = unsafeJso<Options>()
    options.value = 2
    return options
}

fun main() {
    println(optionsWithApply().value)
    println(optionsWithoutApply().value)
}
