package com.example.kmp_mvvm

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count += 1 }) {
        Text("Count: $count")
    }
}
