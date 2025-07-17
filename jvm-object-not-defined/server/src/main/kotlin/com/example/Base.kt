package com.example

interface Base {
    val id: String

    val priority: Int
        get() = priorities[this]!!

    companion object {
        // order is important for sorting by priority
        val allFormats: List<Base> =
            listOf(
                Derived,
            ).also { list ->
                list.forEach {
                    requireNotNull(it.id)
                }
            }

        private val priorities: Map<Base, Int> by lazy {
            allFormats
                .mapIndexed { index, format -> format to -index }
                .toMap()
        }
    }
}
