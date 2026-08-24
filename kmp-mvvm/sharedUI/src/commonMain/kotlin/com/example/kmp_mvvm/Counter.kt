package com.example.kmp_mvvm

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kmp_mvvm.viewmodel.CounterViewModel
import com.example.kmp_mvvm.viewmodel.CounterViewModelImpl

@Composable
fun Counter(viewModel: CounterViewModel = remember { CounterViewModelImpl() }) {
    val count by viewModel.count.collectAsWiredState()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = viewModel::decrement) { Text("−") }
        TextField(
            value = count.toString(),
            onValueChange = { text -> text.toIntOrNull()?.let(viewModel::setCount) },
            modifier = Modifier.width(96.dp),
        )
        Text(viewModel.countObject.symbol)
        Button(onClick = viewModel::increment) { Text("+") }
        Button(onClick = viewModel::resetCount) { Text("Reset") }
    }
}
