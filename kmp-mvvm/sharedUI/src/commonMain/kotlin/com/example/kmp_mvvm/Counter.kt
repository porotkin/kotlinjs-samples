package com.example.kmp_mvvm

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun Counter(viewModel: CounterViewModel = remember { CounterViewModel() }) {
    val count by viewModel.count.collectAsStateWithLifecycle()
    Button(onClick = viewModel::increment) {
        Text("Count: $count")
    }
}
