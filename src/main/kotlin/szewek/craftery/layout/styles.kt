package szewek.craftery.layout

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

val colorsDark = darkColors(
    primary = Color(0xff394739),
    onPrimary = Color.White
)

@Composable
fun AppTheme(content: @Composable () -> Unit) = DesktopMaterialTheme(
    colors = colorsDark
) {
    CompositionLocalProvider(
        LocalHoverColor provides MaterialTheme.colors.onSurface.copy(0.12f),
        content = content
    )
}