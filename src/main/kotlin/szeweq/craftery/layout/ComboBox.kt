package szeweq.craftery.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szeweq.craftery.util.bindValue

/**
 * A combo box with custom values and labels.
 */
@Composable
fun <T> ComboBox(name: String, setValue: (T) -> Unit, vararg pairs: Pair<String, T>) = ComboBoxLayout(name) { dismiss ->
    val mod = Modifier.hover()
    for ((txt, value) in pairs) {
        ComboBoxItem(txt, { setValue(value); dismiss() }, mod)
    }
}

/**
 * A combo box with specified values (also used as labels).
 */
@Composable
fun ComboBox(name: String, setValue: (String) -> Unit, values: Iterable<String>) = ComboBoxLayout(name) { dismiss ->
    val mod = Modifier.hover()
    for (txt in values) {
        ComboBoxItem(txt, { setValue(txt); dismiss() }, mod)
    }
}

@Composable
fun ComboBoxItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp)
) = DropdownMenuItem(
    onClick,
    modifier.heightIn(min = 32.dp, max = 32.dp),
    contentPadding = contentPadding,
    content = ComposeScopeText(text, fontSize = 13.sp)
)

@Composable
private fun ComboBoxLayout(name: String, content: @Composable ColumnScope.(() -> Unit) -> Unit) = Box {
    val menuToggle = remember { mutableStateOf(false) }
    val dismiss = menuToggle.bindValue(false)
    DesktopButton(menuToggle.bindValue(true), Modifier.heightIn(24.dp), contentPadding = PaddingValues(8.dp, 4.dp)) {
        Text(name, fontSize = 12.sp, letterSpacing = 0.5.sp)
        Icon(Icons.Default.ArrowDropDown, "Dropdown", Modifier.size(16.dp))
    }
    DropdownMenu(menuToggle.value, dismiss) {
        content(dismiss)
    }
}