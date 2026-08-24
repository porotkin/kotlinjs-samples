package com.example.kmp_mvvm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CounterViewModel {
    val count: StateFlow<Int>
        field = MutableStateFlow(0)

    fun increment() {
        count.update { it + 1 }
    }
}
