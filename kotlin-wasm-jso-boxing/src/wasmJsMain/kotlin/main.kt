@file:OptIn(ExperimentalWasmJsInterop::class)

import kotlin.time.measureTime

fun consume(options: Options): Unit =
    js("void options")

fun main() {
    repeat(3) {
        val time = measureTime { repeat(1_000_000) { consume(withoutScopeFunction()) } }
        println("withoutScopeFunction: ${time.inWholeMilliseconds} ms")
    }
    repeat(3) {
        val time = measureTime { repeat(1_000_000) { consume(withApply()) } }
        println("withApply: ${time.inWholeMilliseconds} ms")
    }

    withAlso()
    withTakeIf()
    withLet()
    withRun()
    withWith()
    discarded()
}
