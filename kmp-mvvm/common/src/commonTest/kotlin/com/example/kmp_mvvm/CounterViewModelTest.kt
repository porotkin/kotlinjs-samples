package com.example.kmp_mvvm

import com.example.kmp_mvvm.viewmodel.CounterViewModelImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class CounterViewModelTest {

    @Test
    fun actionsUpdateCount() {
        val viewModel = CounterViewModelImpl(initialCount = 10)
        assertEquals(10, viewModel.count.value)

        viewModel.increment()
        viewModel.increment()
        viewModel.decrement()
        assertEquals(11, viewModel.count.value)

        viewModel.setCount(42)
        assertEquals(42, viewModel.count.value)

        viewModel.resetCount()
        assertEquals(10, viewModel.count.value)
    }
}
