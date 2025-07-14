package sentry.react

import js.array.ReadonlyArray
import js.objects.JsPlainObject

@JsPlainObject
external interface SentryInitOptions {
    val dsn: String
    val sendDefaultPii: Boolean

    val integrations: ReadonlyArray<Any>

    // Session Replay
    val replaysSessionSampleRate: Double
    val replaysOnErrorSampleRate: Double
}
