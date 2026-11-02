package sample.reactcompiler

import js.array.ReadonlyArray
import react.useCallback
import react.useMemo

/**
 * Case 03: `useMemo` and `useCallback` Lowering with Reversed Parameters & Spilled Temporaries
 *
 * - **Cause:** In `kotlin-react`, `inline fun useMemo(vararg dependencies: Any?, noinline callback: () -> T)`
 *   and `inline fun useCallback(vararg dependencies: Any?, callback: T)` reverse the parameter order relative
 *   to React's `useMemoRaw(callback, dependencies)`. To preserve Kotlin's left-to-right evaluation order
 *   (and even in 0-arg `useMemo { ... }` where `emptyArray()` is inlined), the Kotlin IR inliner spills
 *   both arguments into local variables:
 *   ```js
 *   var tmp0 = [items, query];
 *   var callback = () => ...;
 *   var filtered = useMemo(callback, tmp0);
 *   ```
 *   When forwarding `vararg elements` (as in [useMemoizedList]), `dependencies` lowers to `var tmp0 = elements.slice()`.
 * - **React Compiler Behavior:** Fails in `BuildHIR` / `DropManualMemoization` with two hard errors:
 *   - `Error: Expected the dependency list for useMemo to be an array literal.`
 *   - `Error: Expected the first argument to be an inline function expression.`
 * - **Fix:** Make `useMemo` / `useCallback` inline pass-throughs (`callback()` / `callback`) when React Compiler
 *   is used, or inline `callback` and `tmp0` in the Vite pre-transform plugin.
 */
fun useSearchResults(
    items: ReadonlyArray<String>,
    query: String,
    onSelect: (String) -> Unit,
): Pair<ReadonlyArray<String>, (Int) -> Unit> {
    val defaultHeader = useMemo { "Results" }

    val filtered = useMemo(items, query) {
        items.filter { it.contains(query, ignoreCase = true) }.toTypedArray()
    }

    val handleSelectIndex = useCallback(filtered, onSelect) { index: Int ->
        val selected = filtered.getOrNull(index) ?: defaultHeader
        onSelect(selected)
    }

    return filtered to handleSelectIndex
}

fun <T> useMemoizedList(
    vararg elements: T,
): List<T> =
    useMemo(*elements) {
        elements.asList()
    }
