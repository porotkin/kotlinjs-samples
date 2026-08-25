import com.example.kmp_mvvm.model.CountObject
import mui.material.*
import mui.system.responsive
import react.FC
import react.Fragment
import react.Props
import react.create
import web.html.InputType
import web.html.number

external interface CounterProps : Props {
    var countObject: CountObject
}

val Counter = FC<CounterProps> { props ->
    val counter = useCounterViewModel(props.countObject)

    Grid {
        container = true
        spacing = responsive(2)

        Button {
            variant = ButtonVariant.outlined
            onClick = counter.onDecrement
            disabled = !counter.canDecrement

            +"−"
        }

        OutlinedTextField {
            type = InputType.number
            value = counter.count.toString()
            error = counter.countError != null
            helperText = counter.countError?.let {
                Fragment.create {
                    +it
                }
            }
            onChange = counter.onSetCount
        }

        +" ${counter.symbol} "

        Button {
            variant = ButtonVariant.outlined
            onClick = counter.onIncrement

            +"+"
        }

        Button {
            onClick = counter.onReset
            disabled = !counter.isDirty

            +"Reset"
        }

        Button {
            variant = ButtonVariant.contained
            onClick = counter.onSave
            disabled = !counter.canSave
            loading = counter.isSaving

            +"Save"
        }

        counter.saveError?.let { message ->
            FormHelperText {
                error = true

                +message
            }
        }
    }
}

private val OutlinedTextField = TextField.unsafeCast<FC<OutlinedTextFieldProps>>()
