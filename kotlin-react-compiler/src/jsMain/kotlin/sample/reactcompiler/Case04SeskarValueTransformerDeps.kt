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
 * - **Mitigation:** Unbox `value class` / [Duration] values into local `val`s (in a `vararg Any?` dependency
 *   list a value class is boxed into a fresh object every render) and let the IR plugin rewrite `useMemo` /
 *   `useCallback` into `useMemoRaw(callback, [deps])` / `useCallbackRaw(callback, [deps])`.
 */
fun useUserRequestUrl(
    userId: UserId,
    timeout: Duration,
    version: Long,
): () -> String {
    val userIdValue = userId.id
    val timeoutMs = timeout.inWholeMilliseconds

    val endpoint = useMemo(userIdValue, timeoutMs, version) {
        "/api/users/$userIdValue?timeout=$timeoutMs&v=$version"
    }

    return useCallback(userIdValue, endpoint) {
        "GET $endpoint (user=$userIdValue)"
    }
}
