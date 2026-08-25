import com.example.kmp_mvvm.viewmodel.HasViewModelScope
import kotlinx.coroutines.awaitCancellation
import react.use.useConstant
import react.useEffectOnce

fun <VM : HasViewModelScope> useViewModel(create: () -> VM): VM {
    val viewModel = useConstant(create)

    useEffectOnce {
        try {
            awaitCancellation()
        } finally {
            viewModel.close()
        }
    }

    return viewModel
}
