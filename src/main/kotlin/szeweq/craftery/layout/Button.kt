package szeweq.craftery.layout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import szeweq.craftery.util.rememberInitialState

@Composable
fun DesktopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val hover = rememberInitialState(false)
    Button(
        onClick,
        modifier.hoverState(hover.component2()),
        enabled,
        interactionSource,
        elevation,
        shape,
        border,
        hoverButtonColors(hover),
        contentPadding,
        content
    )
}

@Composable
fun hoverButtonColors(
    hover: State<Boolean>,
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = contentColorFor(backgroundColor),
    disabledBackgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        .compositeOver(MaterialTheme.colors.surface),
    disabledContentColor: Color = MaterialTheme.colors.onSurface
        .copy(alpha = ContentAlpha.disabled)
): ButtonColors = HoverButtonColors(
    hover,
    defaultColors = backgroundColor to contentColor,
    disabledColors = disabledBackgroundColor to disabledContentColor,
    hoverColor = LocalHoverColor.current
)
