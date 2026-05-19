package kfc.test.jso

import js.array.ReadonlyArray

typealias Notifications = ReadonlyArray<String>

internal fun Notifications.withRemoved(
    item: String,
): Notifications =
    filter { it != item }.toTypedArray()
