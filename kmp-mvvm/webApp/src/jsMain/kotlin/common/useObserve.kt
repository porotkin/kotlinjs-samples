import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import react.Cleanup
import react.useCallback
import react.useSyncExternalStore

@Suppress("UNCHECKED_CAST")
fun <T> useObserve(state: Flow<T>): T {
    val stateFlow = state as StateFlow<T>
    val subscribe = useCallback(stateFlow) { onStoreChange: () -> Unit ->
        stateFlow.subscribe(onStoreChange)
    }
    return useSyncExternalStore(subscribe, getSnapshot = { stateFlow.value })
}

private fun StateFlow<*>.subscribe(
    onStoreChange: () -> Unit,
): Cleanup {
    val job = onEach { onStoreChange() }
        .launchIn(CoroutineScope(Dispatchers.Unconfined))

    return { job.cancel() }
}
