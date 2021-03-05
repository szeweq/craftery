package szewek.craftery.layout

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

val defaultScrollbarOnDark = ScrollbarStyle(
    minimalHeight = 16.dp,
    thickness = 8.dp,
    shape = RectangleShape,
    hoverDurationMillis = 0,
    unhoverColor = Color.White.copy(alpha = 0.12f),
    hoverColor = Color.White.copy(alpha = 0.12f)
)

val colorsDark = darkColors(
    primary = Color(0xff394739),
    onPrimary = Color.White
)

@Composable
fun AppTheme(content: @Composable () -> Unit) = DesktopMaterialTheme(colors = colorsDark) {
    CompositionLocalProvider(
        LocalHoverColor provides MaterialTheme.colors.onSurface.copy(0.12f),
        content = content
    )
}