import com.example.kmp_mvvm.model.CountObject
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import web.html.InputType
import web.html.number

external interface CounterProps : Props {
    var countObject: CountObject
}

val Counter = FC<CounterProps> { props ->
    val viewModel = useCounterViewModel(props.countObject)

    div {
        button {
            onClick = viewModel.onDecrement
            disabled = !viewModel.canDecrementValue

            +"−"
        }

        input {
            type = InputType.number
            value = viewModel.countValue.toString()
            onChange = viewModel.onSetCount
        }

        +" ${viewModel.countObject.symbol} "

        button {
            onClick = viewModel.onIncrement

            +"+"
        }

        button {
            onClick = viewModel.onResetCount

            +"Reset"
        }

        viewModel.countErrorValue?.let { message ->
            div {
                +message
            }
        }
    }
}
