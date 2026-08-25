package com.example.kmp_mvvm.viewmodel

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.*

fun <R> computed(compute: () -> R): StateFlow<R> =
    ComputedStateFlow(compute)

private object Unset

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class ComputedStateFlow<R>(
    private val compute: () -> R,
) : StateFlow<R> {
    override val value: R
        get() {
            val result = compute()
            DependencyTracker.record(this, result)
            return result
        }

    override val replayCache: List<R>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        var last: Any? = Unset
        while (true) {
            val (result, reads) = DependencyTracker.track(compute)

            if (last == Unset || result != last) {
                collector.emit(result)
                last = result
            }
            if (reads.isEmpty()) awaitCancellation()

            reads.entries
                .map { (source, seen) -> source.filter { it != seen } }
                .merge()
                .first()
        }
    }
}
