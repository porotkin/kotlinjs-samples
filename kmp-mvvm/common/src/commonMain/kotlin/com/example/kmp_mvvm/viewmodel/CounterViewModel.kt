package com.example.kmp_mvvm.viewmodel

import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.usecases.DecrementCount
import com.example.kmp_mvvm.usecases.IncrementCount
import com.example.kmp_mvvm.usecases.ResetCount
import kotlinx.coroutines.flow.MutableStateFlow

interface CounterViewModel {
    val countObject: CountObject

    val count: ObservableProperty<Int>
    val countError: ObservableProperty<String?>
    val canDecrement: ObservableProperty<Boolean>

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
    override val countError = MutableStateFlow(countObject.validate(initialCount))
    override val canDecrement = MutableStateFlow(canDecrementFrom(initialCount))

    override fun increment() {
        mutate { incrementCount(it) }
    }

    override fun decrement() {
        if (!canDecrement.value) return
        mutate { decrementCount(it) }
    }

    override fun setCount(count: Int) {
        mutate { count }
    }

    override fun resetCount() {
        mutate { restoreInitialCount() }
    }

    private fun mutate(transform: (Int) -> Int) {
        val newCount = transform(count.value)
        count.value = newCount
        countError.value = countObject.validate(newCount)
        canDecrement.value = canDecrementFrom(newCount)
    }

    private fun canDecrementFrom(count: Int): Boolean =
        countObject.validate(decrementCount(count)) == null
}
