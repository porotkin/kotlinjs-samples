@file:OptIn(ExperimentalWasmJsInterop::class)

import kotlin.time.measureTime

fun consume(options: Options): Unit =
    js("void options")

fun main() {
    repeat(3) {
        val apply = measureTime { repeat(1_000_000) { consume(withApply()) } }
        val plain = measureTime { repeat(1_000_000) { consume(withoutScopeFunction()) } }
        println("withApply: ${apply.inWholeMilliseconds} ms, withoutScopeFunction: ${plain.inWholeMilliseconds} ms")
    }

    withAlso()
    withTakeIf()
    withLet()
    withRun()
    withWith()
    discarded()
}
