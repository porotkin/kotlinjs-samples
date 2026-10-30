fun classify(
    x: Int,
    y: Int,
): String = when {
    x == 0 -> "x is 0"
    x == 1 -> "x is 1"
    y == 0 -> "y is 0" // dropped in production: `0` is already a case of the switch over `x`
    else -> "else"
}

fun main() {
    println(classify(x = 5, y = 0)) // expected: y is 0
}
