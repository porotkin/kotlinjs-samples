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
 * - **Mitigation:** The IR plugin rewrites `useMemo` / `useCallback` into `useMemoRaw(callback, [deps])` /
 *   `useCallbackRaw(callback, [deps])`. A spread dependency list ([useMemoizedList]) can't become an array
 *   literal, so it keeps the [useMemoInline] pass-through.
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
): List<T> {
    return useMemoInline(*elements) {
        elements.asList()
    }
}
