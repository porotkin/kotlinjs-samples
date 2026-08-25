import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.viewmodel.CounterViewModel
import com.example.kmp_mvvm.viewmodel.CounterViewModelImpl
import react.dom.events.ChangeEventHandler
import react.dom.events.MouseEventHandler
import react.useCallback
import web.html.HTMLButtonElement
import web.html.HTMLInputElement

interface ReactCounterViewModel : CounterViewModel {
    val countValue: Int
    val countErrorValue: String?
    val canDecrementValue: Boolean
    val isDirtyValue: Boolean
    val isSavingValue: Boolean
    val canSaveValue: Boolean
    val saveErrorValue: String?

    val onIncrement: MouseEventHandler<HTMLButtonElement>
    val onDecrement: MouseEventHandler<HTMLButtonElement>
    val onSetCount: ChangeEventHandler<HTMLInputElement, HTMLInputElement>
    val onReset: MouseEventHandler<HTMLButtonElement>
    val onSave: MouseEventHandler<HTMLButtonElement>
}

fun useCounterViewModel(countObject: CountObject): ReactCounterViewModel {
    val viewModel: CounterViewModel = useViewModel {
        CounterViewModelImpl(
            CounterViewModelImpl.Params(
                countObject = countObject,
                onSaved = { saved -> println("saved $saved ${countObject.symbol} — invalidate query") },
            ),
        )
    }

    val count = useObserve(viewModel.count)
    val countError = useObserve(viewModel.countError)
    val canDecrement = useObserve(viewModel.canDecrement)
    val isDirty = useObserve(viewModel.isDirty)
    val isSaving = useObserve(viewModel.isSaving)
    val canSave = useObserve(viewModel.canSave)
    val saveErrorState = useObserve(viewModel.saveError)

    val increment: MouseEventHandler<HTMLButtonElement> = useCallback(viewModel) {
        viewModel.increment()
    }
    val decrement: MouseEventHandler<HTMLButtonElement> = useCallback(viewModel) {
        viewModel.decrement()
    }
    val setCount: ChangeEventHandler<HTMLInputElement, HTMLInputElement> = useCallback(viewModel) { event ->
        val value = event.target.valueAsNumber
        if (!value.isNaN()) viewModel.setCount(value.toInt())
    }
    val reset: MouseEventHandler<HTMLButtonElement> = useCallback(viewModel) {
        viewModel.resetCount()
    }
    val save: MouseEventHandler<HTMLButtonElement> = useCallback(viewModel) {
        viewModel.save()
    }

    return object : ReactCounterViewModel, CounterViewModel by viewModel {
        override val countValue = count
        override val countErrorValue = countError
        override val canDecrementValue = canDecrement
        override val isDirtyValue = isDirty
        override val isSavingValue = isSaving
        override val canSaveValue = canSave
        override val saveErrorValue = saveErrorState
        override val onIncrement = increment
        override val onDecrement = decrement
        override val onSetCount = setCount
        override val onReset = reset
        override val onSave = save
    }
}
