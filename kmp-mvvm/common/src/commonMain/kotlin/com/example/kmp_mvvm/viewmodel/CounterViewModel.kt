package com.example.kmp_mvvm.viewmodel

import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.service.FakeCounterService
import com.example.kmp_mvvm.usecases.DecrementCount
import com.example.kmp_mvvm.usecases.IncrementCount
import com.example.kmp_mvvm.usecases.SaveCount

interface CounterViewModel {
    val countObject: CountObject

    val count: ObservableProperty<Int>
    val countError: ObservableProperty<String?>
    val canDecrement: ObservableProperty<Boolean>

    val isDirty: ObservableProperty<Boolean>
    val isSaving: ObservableProperty<Boolean>
    val canSave: ObservableProperty<Boolean>
    val saveError: ObservableProperty<String?>

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

    private val saved = signal(params.initialCount)

    override val count = signal(params.initialCount)
    override val countError = computed { countObject.validate(count.value) }
    override val canDecrement = computed { countObject.validate(decrementCount(count.value)) == null }

    override val isDirty = computed { count.value != saved.value }
    override val isSaving = signal(false)
    override val saveError = signal<String?>(null)
    override val canSave = computed { isDirty.value && countError.value == null && !isSaving.value }

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
