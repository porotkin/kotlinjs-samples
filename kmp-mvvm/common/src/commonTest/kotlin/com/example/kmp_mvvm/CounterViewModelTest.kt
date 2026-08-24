package com.example.kmp_mvvm

import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.viewmodel.CounterViewModelImpl
import kotlin.test.*

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

    @Test
    fun applesValidation() {
        val viewModel = CounterViewModelImpl(CountObject.APPLES)
        assertFalse(viewModel.canDecrement.value)

        viewModel.decrement()
        assertEquals(0, viewModel.count.value)

        viewModel.increment()
        assertTrue(viewModel.canDecrement.value)
        assertNull(viewModel.countError.value)

        viewModel.setCount(-5)
        assertEquals(-5, viewModel.count.value)
        assertNotNull(viewModel.countError.value)
        assertFalse(viewModel.canDecrement.value)
    }
}
