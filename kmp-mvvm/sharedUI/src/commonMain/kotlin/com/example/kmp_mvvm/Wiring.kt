package com.example.kmp_mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.kmp_mvvm.viewmodel.HasViewModelScope

@Composable
fun <VM : HasViewModelScope> rememberViewModel(create: () -> VM): VM {
    val viewModel = remember { create() }
    DisposableEffect(viewModel) {
        onDispose { viewModel.close() }
    }
    return viewModel
}
