import com.example.kmp_mvvm.viewmodel.ObservableProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import react.Cleanup
import react.useCallback
import react.useSyncExternalStore

@Suppress("UNCHECKED_CAST")
fun <T> useObserve(state: ObservableProperty<T>): T {
    val stateFlow = state as StateFlow<T>

    val subscribe = useCallback(stateFlow) { onStoreChange: () -> Unit ->
        val scope = CoroutineScope(Dispatchers.Unconfined)

        stateFlow
            .onEach { onStoreChange() }
            .launchIn(scope)

        val cleanup: Cleanup = { scope.cancel() }
        cleanup
    }

    return useSyncExternalStore(subscribe, getSnapshot = { stateFlow.value })
}
