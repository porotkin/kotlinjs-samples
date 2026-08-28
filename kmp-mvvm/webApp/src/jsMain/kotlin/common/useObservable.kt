import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import react.Cleanup
import react.useCallback
import react.useMemo
import react.useSyncExternalStore

fun <T> useObservable(state: StateFlow<T>): T {
    val snapshot = useMemo(state) { Snapshot(state) }
    val subscribe = useCallback(snapshot) { onStoreChange: () -> Unit ->
        snapshot.subscribe(onStoreChange)
    }
    return useSyncExternalStore(subscribe, getSnapshot = { snapshot.value })
}

private class Snapshot<T>(private val source: StateFlow<T>) {
    var value = source.value
        private set

    fun subscribe(onStoreChange: () -> Unit): Cleanup {
        val job = source
            .onEach { next ->
                if (next != value) {
                    value = next
                    onStoreChange()
                }
            }
            .launchIn(CoroutineScope(Dispatchers.Unconfined))

        return { job.cancel() }
    }
}
