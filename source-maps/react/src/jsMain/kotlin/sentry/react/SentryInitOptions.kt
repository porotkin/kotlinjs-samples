package sentry.react

import js.array.ReadonlyArray
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface SentryInitOptions {
    val dsn: String
    val sendDefaultPii: Boolean

    val integrations: ReadonlyArray<Any>

    // Session Replay
    val replaysSessionSampleRate: Double
    val replaysOnErrorSampleRate: Double
}
