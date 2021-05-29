package szewek.craftery.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> ComboBox(name: String, current: MutableState<T>, vararg pairs: Pair<String, T>) = ComboBoxLayout(name) { dismiss ->
    val hoverColor = MaterialTheme.colors.onSurface.copy(0.2f)
    for ((txt, value) in pairs) {
        DropdownMenuItem({ current.value = value; dismiss() }, Modifier.hover(hoverColor)) { Text(txt, fontSize = 14.sp) }
    }
}

@Composable
fun ComboBox(name: String, current: MutableState<String>, values: Iterable<String>) = ComboBoxLayout(name) { dismiss ->
    val hoverColor = MaterialTheme.colors.onSurface.copy(0.2f)
    for (txt in values) {
        DropdownMenuItem({ current.value = txt; dismiss() }, Modifier.hover(hoverColor)) { Text(txt, fontSize = 14.sp) }
    }
}

@Composable
private fun ComboBoxLayout(name: String, content: @Composable ColumnScope.(() -> Unit) -> Unit) = Box {
    val menuToggle = remember { mutableStateOf(false) }
    val dismiss = { menuToggle.value = false }
    Button({ menuToggle.value = true }, Modifier.heightIn(24.dp), contentPadding = PaddingValues(8.dp, 4.dp)) {
        Text(name, fontSize = 12.sp, letterSpacing = 0.5.sp)
        Icon(Icons.Default.ArrowDropDown, "Dropdown", Modifier.size(16.dp))
    }
    DropdownMenu(menuToggle.value, dismiss) { content(dismiss) }
}