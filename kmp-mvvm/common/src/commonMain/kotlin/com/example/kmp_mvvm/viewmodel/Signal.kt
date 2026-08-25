package com.example.kmp_mvvm.viewmodel

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun <T> signal(initial: T): MutableSignal<T> =
    MutableSignal(MutableStateFlow(initial))

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
class MutableSignal<T> internal constructor(
    private val backing: MutableStateFlow<T>,
) : StateFlow<T> by backing {
    override var value: T
        get() {
            val current = backing.value
            DependencyTracker.record(this, current)
            return current
        }
        set(newValue) {
            backing.value = newValue
        }
}

fun <T> MutableSignal<T>.update(transform: (T) -> T) {
    value = transform(value)
}

internal expect object ActiveTracker {
    var reads: MutableMap<StateFlow<*>, Any?>?
}

internal object DependencyTracker {
    fun record(source: StateFlow<*>, valueSeen: Any?) {
        val reads = ActiveTracker.reads ?: return
        if (source !in reads) reads[source] = valueSeen
    }

    fun <R> track(compute: () -> R): Pair<R, Map<StateFlow<*>, Any?>> {
        val previous = ActiveTracker.reads
        val reads = linkedMapOf<StateFlow<*>, Any?>()
        ActiveTracker.reads = reads
        try {
            return compute() to reads
        } finally {
            ActiveTracker.reads = previous
        }
    }
}
