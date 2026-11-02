package sample.reactcompiler

import js.array.ReadonlyArray
import react.useState

/**
 * Case 01: `var` Variable Declarations (`BuildHIR` bailout in Kotlin < 2.5)
 *
 * - **Cause:** In Kotlin < 2.5, the Kotlin/JS IR backend emits `var` for all local variables
 *   and compiler temporaries (`var tmp = ...`, `var _destruct = ...`) even when targeting ES2015 modules.
 * - **React Compiler Behavior:** `BuildHIR::lowerStatement` only supports `let` and `const` in
 *   `VariableDeclaration` and immediately bails out with:
 *   `Todo: (BuildHIR::lowerStatement) Handle var kinds in VariableDeclaration`
 * - **Fix:** Fixed in Kotlin 2.5 (`KT-67574`, emitting `const` / `let`), or mitigated pre-2.5 by
 *   rewriting `var` declarations to `let` in the Vite pre-transform plugin.
 */
fun useFilteredLabels(
    items: ReadonlyArray<String>,
    prefix: String,
): ReadonlyArray<String> {
    val (normalizedPrefix) = useState(prefix.trim())
    val hasPrefix = normalizedPrefix.isNotEmpty()
    return if (hasPrefix) {
        items.filter { it.startsWith(normalizedPrefix) }.toTypedArray()
    } else {
        items
    }
}
