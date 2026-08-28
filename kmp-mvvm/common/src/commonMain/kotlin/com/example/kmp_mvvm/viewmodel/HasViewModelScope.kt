package com.example.kmp_mvvm.viewmodel

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface HasViewModelScope {
    val uiScope: CoroutineScope

    fun launchUi(block: suspend CoroutineScope.() -> Unit): Job =
        uiScope.launch(block = block)

    fun close()
}

class DefaultViewModelScopeProvider(
    context: CoroutineContext = Dispatchers.Main,
    onError: ((Throwable) -> Unit)? = null,
) : HasViewModelScope {
    override val uiScope = CoroutineScope(
        context + SupervisorJob() + (onError?.let {
            CoroutineExceptionHandler { _, throwable -> it(throwable) }
        } ?: EmptyCoroutineContext),
    )

    override fun close() {
        uiScope.cancel()
    }
}
