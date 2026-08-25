package com.example.kmp_mvvm.viewmodel

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface HasViewModelScope {
    val uiScope: CoroutineScope

    fun launchUi(block: suspend CoroutineScope.() -> Unit): Job =
        uiScope.launch(block = block)

    fun close()
}

class DefaultViewModelScopeProvider(
    context: CoroutineContext = Dispatchers.Main,
    onError: (Throwable) -> Unit = { it.printStackTrace() },
) : HasViewModelScope {
    override val uiScope = CoroutineScope(
        context + SupervisorJob() + CoroutineExceptionHandler { _, throwable -> onError(throwable) },
    )

    override fun close() {
        uiScope.cancel()
    }
}
