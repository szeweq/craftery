package szewek.craftery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent
) = BasicTextField(
    value,
    onValueChange,
    modifier.background(background, MaterialTheme.shapes.small).padding(4.dp),
    singleLine = true,
    textStyle = TextStyle(MaterialTheme.colors.onSurface),
    cursorBrush = SolidColor(MaterialTheme.colors.onSurface)
)