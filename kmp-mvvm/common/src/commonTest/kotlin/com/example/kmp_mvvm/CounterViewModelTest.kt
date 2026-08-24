package com.example.kmp_mvvm

import kotlin.test.Test
import kotlin.test.assertEquals

class CounterViewModelTest {

    @Test
    fun incrementIncreasesCount() {
        val viewModel = CounterViewModel()
        assertEquals(0, viewModel.count.value)
        viewModel.increment()
        viewModel.increment()
        assertEquals(2, viewModel.count.value)
    }
}
