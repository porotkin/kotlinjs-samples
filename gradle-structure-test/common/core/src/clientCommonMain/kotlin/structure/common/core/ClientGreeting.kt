package structure.common.core

fun clientGreeting(surface: String): String =
    "${commonGreeting(surface).audience} from a client"
