package com.example.kmp_mvvm

import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.service.CounterService
import com.example.kmp_mvvm.usecases.SaveCount
import com.example.kmp_mvvm.viewmodel.CounterViewModelImpl
import com.example.kmp_mvvm.viewmodel.DefaultViewModelScopeProvider
import kotlinx.coroutines.Dispatchers
import kotlin.test.*

class CounterViewModelTest {

    private fun testViewModel(
        countObject: CountObject = CountObject.APPLES,
        initialCount: Int = 0,
        service: CounterService = CounterService { _, count -> count },
    ) = CounterViewModelImpl(
        params = CounterViewModelImpl.Params(countObject, initialCount),
        scopeProvider = DefaultViewModelScopeProvider(Dispatchers.Unconfined),
        saveCount = SaveCount(service),
    )

    @Test
    fun actionsUpdateCount() {
        val viewModel = testViewModel(initialCount = 10)
        assertEquals(10, viewModel.count.value)
        assertFalse(viewModel.isDirty.value)

        viewModel.increment()
        viewModel.increment()
        viewModel.decrement()
        assertEquals(11, viewModel.count.value)
        assertTrue(viewModel.isDirty.value)

        viewModel.setCount(42)
        assertEquals(42, viewModel.count.value)

        viewModel.resetCount()
        assertEquals(10, viewModel.count.value)
        assertFalse(viewModel.isDirty.value)
    }

    @Test
    fun applesValidation() {
        val viewModel = testViewModel()
        assertFalse(viewModel.canDecrement.value)

        viewModel.decrement()
        assertEquals(0, viewModel.count.value)

        viewModel.setCount(-5)
        assertNotNull(viewModel.countError.value)
        assertFalse(viewModel.canSave.value)
    }

    @Test
    fun saveMovesBaselineAndNotifies() {
        var savedNotification: Int? = null
        val viewModel = CounterViewModelImpl(
            params = CounterViewModelImpl.Params(
                CountObject.APPLES,
                initialCount = 10,
                onSaved = { savedNotification = it },
            ),
            scopeProvider = DefaultViewModelScopeProvider(Dispatchers.Unconfined),
            saveCount = SaveCount(CounterService { _, count -> count }),
        )

        viewModel.setCount(42)
        assertTrue(viewModel.canSave.value)

        viewModel.save()
        assertEquals(42, savedNotification)
        assertFalse(viewModel.isDirty.value)

        viewModel.resetCount()
        assertEquals(42, viewModel.count.value)
    }

    @Test
    fun serverUpdateFollowsWhenCleanKeepsDraftWhenDirty() {
        val viewModel = testViewModel(initialCount = 10)

        viewModel.updateSavedCount(20)
        assertEquals(20, viewModel.count.value)
        assertFalse(viewModel.isDirty.value)

        viewModel.setCount(42)
        viewModel.updateSavedCount(30)
        assertEquals(42, viewModel.count.value)
        assertTrue(viewModel.isDirty.value)

        viewModel.resetCount()
        assertEquals(30, viewModel.count.value)
    }

    @Test
    fun failedSaveKeepsDraftAndReportsError() {
        val viewModel = testViewModel(
            initialCount = 10,
            service = CounterService { _, _ -> error("boom") },
        )

        viewModel.setCount(42)
        viewModel.save()

        assertEquals("boom", viewModel.saveError.value)
        assertEquals(42, viewModel.count.value)
        assertTrue(viewModel.isDirty.value)
    }
}
