package com.example.kmp_mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmp_mvvm.viewmodel.ObservableProperty
import kotlinx.coroutines.flow.StateFlow

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> ObservableProperty<T>.collectAsWiredState(): State<T> =
    (this as StateFlow<T>).collectAsStateWithLifecycle()
