package kfc.test.jso

import js.array.ReadonlyArray
import js.reflect.unsafeCast

typealias Notifications = ReadonlyArray<String>

internal fun Notifications.withRemoved(
    item: String,
): Notifications =
    filter { it != item }.toTypedArray()

@Suppress("unused")
internal val castedValue
    get() = unsafeCast<ReadonlyArray<Any?>>(arrayOf<Any?>())
