package com.example.kmp_mvvm.viewmodel

import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.service.FakeCounterService
import com.example.kmp_mvvm.usecases.DecrementCount
import com.example.kmp_mvvm.usecases.IncrementCount
import com.example.kmp_mvvm.usecases.SaveCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface CounterViewModel {
    val countObject: CountObject

    val count: Flow<Int>
    val countError: Flow<String?>
    val canDecrement: Flow<Boolean>

    val isDirty: Flow<Boolean>
    val isSaving: Flow<Boolean>
    val canSave: Flow<Boolean>
    val saveError: Flow<String?>

    fun increment()
    fun decrement()
    fun setCount(count: Int)

    fun resetCount()
    fun save()
}

class CounterViewModelImpl(
    private val params: Params,
    scopeProvider: HasViewModelScope = DefaultViewModelScopeProvider(),
    private val incrementCount: IncrementCount = IncrementCount(),
    private val decrementCount: DecrementCount = DecrementCount(),
    private val saveCount: SaveCount = SaveCount(FakeCounterService()),
) : CounterViewModel, HasViewModelScope by scopeProvider {

    class Params(
        val countObject: CountObject = CountObject.APPLES,
        val initialCount: Int = 0,
        val onSaved: suspend (Int) -> Unit = {},
    )

    override val countObject = params.countObject

    private val saved = MutableStateFlow(params.initialCount)

    override val count = MutableStateFlow(params.initialCount)
    override val countError = count.derived { countObject.validate(it) }
    override val canDecrement = count.derived { countObject.validate(decrementCount(it)) == null }

    override val isDirty = derived(count, saved) { current, baseline -> current != baseline }
    override val isSaving = MutableStateFlow(false)
    override val saveError = MutableStateFlow<String?>(null)
    override val canSave = derived(isDirty, countError, isSaving) { dirty, error, saving ->
        dirty && error == null && !saving
    }

    override fun increment() {
        count.update { incrementCount(it) }
    }

    override fun decrement() {
        if (!canDecrement.value) return
        count.update { decrementCount(it) }
    }

    override fun setCount(count: Int) {
        this.count.value = count
    }

    override fun resetCount() {
        count.value = saved.value
        saveError.value = null
    }

    override fun save() {
        if (!canSave.value) return
        launchUi {
            isSaving.value = true
            saveError.value = null
            saveCount(countObject, count.value)
                .onSuccess { savedCount ->
                    saved.value = savedCount
                    count.value = savedCount
                    params.onSaved(savedCount)
                }
                .onFailure { saveError.value = it.message }
            isSaving.value = false
        }
    }
}
