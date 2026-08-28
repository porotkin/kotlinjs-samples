package com.example.kmp_mvvm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmp_mvvm.viewmodel.CounterViewModel

@Composable
fun Counter(viewModel: CounterViewModel) {
    val count by viewModel.count.collectAsStateWithLifecycle()
    val countError by viewModel.countError.collectAsStateWithLifecycle()
    val canDecrement by viewModel.canDecrement.collectAsStateWithLifecycle()
    val isDirty by viewModel.isDirty.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()
    val saveError by viewModel.saveError.collectAsStateWithLifecycle()

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = viewModel::decrement, enabled = canDecrement) { Text("−") }
            TextField(
                value = count.toString(),
                onValueChange = { text -> text.toIntOrNull()?.let(viewModel::setCount) },
                isError = countError != null,
                supportingText = countError?.let { message -> { Text(message) } },
                modifier = Modifier.width(96.dp),
            )
            Text(viewModel.countObject.symbol)
            Button(onClick = viewModel::increment) { Text("+") }
            Button(onClick = viewModel::resetCount, enabled = isDirty) { Text("Reset") }
            Button(onClick = viewModel::save, enabled = canSave) {
                Text(if (isSaving) "Saving…" else "Save")
            }
        }
        saveError?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}
