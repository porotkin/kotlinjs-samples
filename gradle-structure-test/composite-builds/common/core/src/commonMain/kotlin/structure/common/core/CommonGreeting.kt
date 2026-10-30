package structure.common.core

data class SharedGreeting(
    val audience: String,
)

fun commonGreeting(audience: String): SharedGreeting =
    SharedGreeting("Hello, $audience")
