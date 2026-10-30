package structure.common.client

import structure.common.core.clientGreeting

fun sharedClientMessage(surface: String): String =
    clientGreeting(surface)
