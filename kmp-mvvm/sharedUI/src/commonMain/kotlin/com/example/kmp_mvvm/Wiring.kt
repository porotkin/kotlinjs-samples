package com.example.kmp_mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmp_mvvm.viewmodel.HasViewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <VM : HasViewModelScope> rememberViewModel(create: () -> VM): VM {
    val viewModel = remember { create() }
    DisposableEffect(viewModel) {
        onDispose { viewModel.close() }
    }
    return viewModel
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> Flow<T>.collectAsWiredState(): State<T> =
    (this as StateFlow<T>).collectAsStateWithLifecycle()
