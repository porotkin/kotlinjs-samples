package sample.reactcompiler

import js.array.ReadonlyArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import react.StateSetter
import react.useEffect
import react.useState
import kotlin.time.Duration.Companion.milliseconds

private fun incrementTickCount(count: Int): Int =
    count + 1

private fun createPolledItemsEffect(
    items: ReadonlyArray<String>,
    setTickCount: StateSetter<Int>,
    onTick: (Int) -> Unit,
): suspend CoroutineScope.() -> Unit = {
    delay(1_000.milliseconds)
    val nonBlank = items.filter { it.isNotBlank() }
    setTickCount(::incrementTickCount)
    onTick(nonBlank.size)
}

/**
 * Case 05: Hoisted Lambdas and Coroutine `slambda`s Inside `use*` Hooks
 *
 * - **Cause:** Inside any custom hook `fun useFoo(...)`, Kotlin/JS names hoisted non-capturing lambdas
 *   and coroutine `slambda` factories by prefixing the enclosing function name:
 *   `usePolledItems$lambda$lambda`, `usePolledItems$slambda`, `usePolledItems$slambda$lambda`.
 * - **Mitigation:** Extract coroutine `slambda` factories ([createPolledItemsEffect]) and non-capturing
 *   state updater helpers ([incrementTickCount]) outside `use*` functions so Kotlin/JS does not emit
 *   `use[A-Z0-9]*` helper names, and prefix any remaining `use*$lambda` / `use*$slambda` helpers with `_`
 *   in the Vite pre-transform plugin.
 */
fun usePolledItems(
    items: ReadonlyArray<String>,
    onTick: (Int) -> Unit,
): Pair<ReadonlyArray<String>, () -> Unit> {
    val (tickCount, setTickCount) = useState(0)

    useEffect(items, onTick, effect = createPolledItemsEffect(items, setTickCount, onTick))

    val filtered = if (tickCount > 0) {
        items.filter { it.isNotBlank() }.toTypedArray()
    } else {
        items
    }

    return filtered to { setTickCount(::incrementTickCount) }
}
