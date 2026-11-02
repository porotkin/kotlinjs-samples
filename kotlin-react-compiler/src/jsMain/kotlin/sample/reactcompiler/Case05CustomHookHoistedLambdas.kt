package sample.reactcompiler

import js.array.ReadonlyArray
import kotlinx.coroutines.delay
import react.useEffect
import react.useState
import kotlin.time.Duration.Companion.milliseconds

/**
 * Case 05: Hoisted Lambdas and Coroutine `slambda`s Inside `use*` Hooks
 *
 * - **Cause:** Inside any custom hook `fun useFoo(...)`, Kotlin/JS names hoisted non-capturing lambdas
 *   and coroutine `slambda` factories by prefixing the enclosing function name:
 *   `usePolledItems$lambda$lambda`, `usePolledItems$slambda`, `usePolledItems$slambda$lambda`.
 * - **React Compiler Behavior:** Because `usePolledItems$...` starts with `use` + uppercase letter
 *   (`/ ^use[A-Z0-9] /`), React Compiler classifies these synthetic helper functions **as React Hooks**:
 *   1. Passing or calling a hoisted lambda (`setTickCount { it + 1 }` -> `usePolledItems$lambda$lambda`)
 *      inside a callback or effect fails with:
 *      `Error: Hooks must be called at the top level in the body of a function component or custom hook, and may not be called within function expressions. Cannot call hook within a function expression.`
 *   2. Calling `usePolledItems$slambda(...)` at the top level of `usePolledItems` causes React Compiler
 *      to treat the coroutine `slambda` factory call as a Hook call, refusing to memoize it.
 * - **Fix:** Avoid `use[A-Z0-9]` prefixes for synthetic hoisted functions in `kotlinc-js`, or prefix
 *   them with `_` (`_usePolledItems$lambda`, `_usePolledItems$slambda`) in the Vite pre-transform plugin.
 */
fun usePolledItems(
    items: ReadonlyArray<String>,
    onTick: (Int) -> Unit,
): Pair<ReadonlyArray<String>, () -> Unit> {
    val (tickCount, setTickCount) = useState(0)

    useEffect(items, onTick) {
        delay(1_000.milliseconds)
        val nonBlank = items.filter { it.isNotBlank() }
        setTickCount { it + 1 }
        onTick(nonBlank.size)
    }

    val filtered = if (tickCount > 0) {
        items.filter { it.isNotBlank() }.toTypedArray()
    } else {
        items
    }

    return filtered to { setTickCount { it + 1 } }
}
