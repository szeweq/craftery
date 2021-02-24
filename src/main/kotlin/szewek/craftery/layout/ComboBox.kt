package szewek.craftery.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> ComboBox(name: String, current: MutableState<T>, vararg pairs: Pair<String, T>) = ComboBoxLayout(name) { dismiss ->
    for ((txt, value) in pairs) {
        DropdownMenuItem({ current.value = value; dismiss() }) { Text(txt) }
    }
}

@Composable
fun ComboBox(name: String, current: MutableState<String>, values: Iterable<String>) = ComboBoxLayout(name) { dismiss ->
    for (txt in values) {
        DropdownMenuItem({ current.value = txt; dismiss() }) { Text(txt) }
    }
}

@Composable
private fun ComboBoxLayout(name: String, content: @Composable ColumnScope.(() -> Unit) -> Unit) = Box {
    val menuToggle = remember { mutableStateOf(false) }
    val dismiss = { menuToggle.value = false }
    Button({ menuToggle.value = true }) {
        Text(name)
        Icon(Icons.Default.ArrowDropDown, "Dropdown")
    }
    DropdownMenu(menuToggle.value, dismiss) { content(dismiss) }
}