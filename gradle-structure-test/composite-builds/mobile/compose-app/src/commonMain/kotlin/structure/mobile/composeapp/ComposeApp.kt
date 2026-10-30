package structure.mobile.composeapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import structure.mobile.shared.mobileSharedTitle

fun composeAppTitle(): String =
    "compose: ${mobileSharedTitle()}"

@Composable
fun App() {
    Text(composeAppTitle())
}
