package com.example.kmp_mvvm.viewmodel

import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.usecases.DecrementCount
import com.example.kmp_mvvm.usecases.IncrementCount
import com.example.kmp_mvvm.usecases.ResetCount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface CounterViewModel {
    val countObject: CountObject

    val count: ObservableProperty<Int>

    fun increment()
    fun decrement()
    fun setCount(count: Int)
    fun resetCount()
}

class CounterViewModelImpl(
    override val countObject: CountObject = CountObject.APPLES,
    initialCount: Int = 0,
    private val incrementCount: IncrementCount = IncrementCount(),
    private val decrementCount: DecrementCount = DecrementCount(),
    private val restoreInitialCount: ResetCount = ResetCount(initialCount),
) : CounterViewModel {
    override val count = MutableStateFlow(initialCount)

    override fun increment() {
        count.update { incrementCount(it) }
    }

    override fun decrement() {
        count.update { decrementCount(it) }
    }

    override fun setCount(count: Int) {
        this.count.value = count
    }

    override fun resetCount() {
        count.value = restoreInitialCount()
    }
}
