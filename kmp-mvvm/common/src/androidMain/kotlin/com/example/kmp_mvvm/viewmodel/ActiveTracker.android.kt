package com.example.kmp_mvvm.viewmodel

import kotlinx.coroutines.flow.StateFlow

internal actual object ActiveTracker {
    private val holder = ThreadLocal<MutableMap<StateFlow<*>, Any?>?>()

    actual var reads: MutableMap<StateFlow<*>, Any?>?
        get() = holder.get()
        set(value) = holder.set(value)
}
