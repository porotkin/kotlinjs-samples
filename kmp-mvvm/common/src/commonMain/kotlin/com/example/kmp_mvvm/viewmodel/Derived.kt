package com.example.kmp_mvvm.viewmodel

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.*

fun <T, R> StateFlow<T>.derived(transform: (T) -> R): StateFlow<R> =
    DerivedStateFlow(this, transform)

@Suppress("UNCHECKED_CAST")
fun <T1, T2, R> derived(
    source1: StateFlow<T1>,
    source2: StateFlow<T2>,
    transform: (T1, T2) -> R,
): StateFlow<R> =
    CombinedStateFlow(listOf(source1, source2)) { values ->
        transform(values[0] as T1, values[1] as T2)
    }

@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, R> derived(
    source1: StateFlow<T1>,
    source2: StateFlow<T2>,
    source3: StateFlow<T3>,
    transform: (T1, T2, T3) -> R,
): StateFlow<R> =
    CombinedStateFlow(listOf(source1, source2, source3)) { values ->
        transform(values[0] as T1, values[1] as T2, values[2] as T3)
    }

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class DerivedStateFlow<T, R>(
    private val source: StateFlow<T>,
    private val transform: (T) -> R,
) : StateFlow<R> {
    override val value: R
        get() = transform(source.value)

    override val replayCache: List<R>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        source.map(transform).distinctUntilChanged().collect(collector)
        awaitCancellation()
    }
}

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class CombinedStateFlow<R>(
    private val sources: List<StateFlow<*>>,
    private val transform: (List<Any?>) -> R,
) : StateFlow<R> {
    override val value: R
        get() = transform(sources.map { it.value })

    override val replayCache: List<R>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        combine(sources) { values -> transform(values.toList()) }
            .distinctUntilChanged()
            .collect(collector)
        awaitCancellation()
    }
}
