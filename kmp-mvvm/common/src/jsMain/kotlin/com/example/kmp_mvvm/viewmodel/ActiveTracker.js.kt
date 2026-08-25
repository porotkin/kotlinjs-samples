package com.example.kmp_mvvm.viewmodel

import kotlinx.coroutines.flow.StateFlow

internal actual object ActiveTracker {
    actual var reads: MutableMap<StateFlow<*>, Any?>? = null
}
