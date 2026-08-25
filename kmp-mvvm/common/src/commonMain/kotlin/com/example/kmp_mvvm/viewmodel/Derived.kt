package com.example.kmp_mvvm.viewmodel

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.*

fun <T, R> StateFlow<T>.derived(transform: (T) -> R): StateFlow<R> =
    map(transform)
        .distinctUntilChanged()
        .withValue { transform(value) }

fun <T1, T2, R> derived(
    source1: StateFlow<T1>,
    source2: StateFlow<T2>,
    transform: (T1, T2) -> R,
): StateFlow<R> =
    combine(source1, source2, transform)
        .distinctUntilChanged()
        .withValue { transform(source1.value, source2.value) }

fun <T1, T2, T3, R> derived(
    source1: StateFlow<T1>,
    source2: StateFlow<T2>,
    source3: StateFlow<T3>,
    transform: (T1, T2, T3) -> R,
): StateFlow<R> =
    combine(source1, source2, source3, transform)
        .distinctUntilChanged()
        .withValue { transform(source1.value, source2.value, source3.value) }

private fun <R> Flow<R>.withValue(getValue: () -> R): StateFlow<R> =
    FlowWithValue(this, getValue)

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class FlowWithValue<R>(
    private val source: Flow<R>,
    private val getValue: () -> R,
) : StateFlow<R> {
    override val value: R
        get() = getValue()

    override val replayCache: List<R>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        source.collect(collector)
        awaitCancellation()
    }
}
