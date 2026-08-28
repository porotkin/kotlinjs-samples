import react.useEffect

fun <T> useExternalValue(value: T, sync: (T) -> Unit) {
    useEffect(value) {
        sync(value)
    }
}
