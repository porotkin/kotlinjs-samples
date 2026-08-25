package com.example.kmp_mvvm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.viewmodel.CounterViewModelImpl

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CountObject.entries.forEach { countObject ->
                Counter(
                    rememberViewModel {
                        CounterViewModelImpl(CounterViewModelImpl.Params(countObject))
                    },
                )
            }
        }
    }
}
