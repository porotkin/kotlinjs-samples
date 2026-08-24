package com.example.kmp_mvvm.model

enum class CountObject(val symbol: String, private val minCount: Int = Int.MIN_VALUE) {
    DEGREES("°"),
    PERCENTAGE("%"),
    APPLES("🍎", minCount = 0),
    ;

    fun validate(count: Int): String? =
        if (count < minCount) "Must be at least $minCount" else null
}
