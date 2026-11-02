package sample.reactcompiler

import react.useCallback
import react.useMemo
import kotlin.time.Duration

value class UserId(
    val id: Int,
)

/**
 * Case 04: Seskar `ValueTransformer` Rewriting Hook Dependency Arrays
 *
 * - **Cause:** When a Kotlin `value class` ([UserId]), [Duration], or [Long] is passed in `dependencies`
 *   to `useMemo`, `useCallback`, or `useEffect`, `seskar.compiler.react.value.backend.ValueTransformer`
 *   rewrites the dependency array elements in IR to unbox value classes (`_UserId___get_id__impl__(userId)`)
 *   or call `.toString()` on `Duration` / `Long`, while the hook callback body still references the original
 *   parameters (`userId`, `timeout`, `version`).
 * - **React Compiler Behavior:** Fails in `BuildHIR` and dependency validation with:
 *   - `Error: Expected the dependency list to be an array of simple expressions (e.g. x, x.y.z, x?.y?.z).`
 *   - `Error: Found missing memoization dependencies.`
 * - **Fix:** Disable `ValueTransformer` for `useMemo` / `useCallback` when React Compiler is active
 *   (or make `useMemo` / `useCallback` pass-throughs in `kotlin-react`), and unbox value classes into
 *   local `val`s before closures if needed for React Compiler's own `$[i] !== dep` comparisons.
 */
fun useUserRequestUrl(
    userId: UserId,
    timeout: Duration,
    version: Long,
): () -> String {
    val endpoint = useMemo(userId, timeout, version) {
        "/api/users/${userId.id}?timeout=${timeout.inWholeMilliseconds}&v=$version"
    }

    return useCallback(userId, endpoint) {
        "GET $endpoint (user=${userId.id})"
    }
}
