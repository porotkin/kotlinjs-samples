package structure.common.core

fun jvmGreeting(service: String): String =
    "${commonGreeting(service).audience} on the JVM"
