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
    val onIncrement: MouseEventHandler<HTMLButtonElement>
    val onDecrement: MouseEventHandler<HTMLButtonElement>
    val onSetCount: ChangeEventHandler<HTMLInputElement, HTMLInputElement>
    val onResetCount: MouseEventHandler<HTMLButtonElement>
}

fun useCounterViewModel(countObject: CountObject): ReactCounterViewModel {
    val viewModel = useViewModel<CounterViewModel> { CounterViewModelImpl(countObject) }
    val count = useObserve(viewModel.count)

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
    val resetCount: MouseEventHandler<HTMLButtonElement> = useCallback(viewModel) {
        viewModel.resetCount()
    }

    return object : ReactCounterViewModel, CounterViewModel by viewModel {
        override val countValue = count
        override val onIncrement = increment
        override val onDecrement = decrement
        override val onSetCount = setCount
        override val onResetCount = resetCount
    }
}
