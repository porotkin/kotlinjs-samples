import com.example.kmp_mvvm.CounterViewModel
import react.FC
import react.dom.html.ReactHTML.button
import react.useEffectOnce
import react.useMemo
import react.useState

val Counter = FC {
    val viewModel = useMemo { CounterViewModel() }
    var count by useState(viewModel.count.value)

    useEffectOnce {
        viewModel.count.collect { count = it }
    }

    button {
        onClick = { viewModel.increment() }
        +"Count: $count"
    }
}
