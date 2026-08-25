import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.viewmodel.CounterViewModel
import com.example.kmp_mvvm.viewmodel.CounterViewModelImpl
import react.dom.events.ChangeEventHandler
import react.dom.events.MouseEventHandler
import react.use.useConstant
import web.html.HTMLButtonElement
import web.html.HTMLElement
import web.html.HTMLInputElement

class CounterState(
    val symbol: String,
    val count: Int,
    val countError: String?,
    val canDecrement: Boolean,
    val isDirty: Boolean,
    val isSaving: Boolean,
    val canSave: Boolean,
    val saveError: String?,
    val onIncrement: MouseEventHandler<HTMLButtonElement>,
    val onDecrement: MouseEventHandler<HTMLButtonElement>,
    val onSetCount: ChangeEventHandler<HTMLElement, *>,
    val onReset: MouseEventHandler<HTMLButtonElement>,
    val onSave: MouseEventHandler<HTMLButtonElement>,
)

fun useCounterViewModel(countObject: CountObject): CounterState {
    val viewModel: CounterViewModel = useViewModel {
        CounterViewModelImpl(
            CounterViewModelImpl.Params(
                countObject = countObject,
                onSaved = { saved -> println("saved $saved ${countObject.symbol} — invalidate query") },
            ),
        )
    }

    val handlers = useConstant {
        object {
            val increment: MouseEventHandler<HTMLButtonElement> = { viewModel.increment() }
            val decrement: MouseEventHandler<HTMLButtonElement> = { viewModel.decrement() }
            val reset: MouseEventHandler<HTMLButtonElement> = { viewModel.resetCount() }
            val save: MouseEventHandler<HTMLButtonElement> = { viewModel.save() }
            val setCount: ChangeEventHandler<HTMLElement, *> = { event ->
                val value = (event.target as HTMLInputElement).valueAsNumber
                if (!value.isNaN()) viewModel.setCount(value.toInt())
            }
        }
    }

    val count = useObserve(viewModel.count)
    val countError = useObserve(viewModel.countError)
    val canDecrement = useObserve(viewModel.canDecrement)
    val isDirty = useObserve(viewModel.isDirty)
    val isSaving = useObserve(viewModel.isSaving)
    val canSave = useObserve(viewModel.canSave)
    val saveError = useObserve(viewModel.saveError)

    return CounterState(
        symbol = viewModel.countObject.symbol,
        count = count,
        countError = countError,
        canDecrement = canDecrement,
        isDirty = isDirty,
        isSaving = isSaving,
        canSave = canSave,
        saveError = saveError,
        onIncrement = handlers.increment,
        onDecrement = handlers.decrement,
        onSetCount = handlers.setCount,
        onReset = handlers.reset,
        onSave = handlers.save,
    )
}
