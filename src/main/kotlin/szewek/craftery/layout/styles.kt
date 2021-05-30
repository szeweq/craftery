package szewek.craftery.layout

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

val colorsDark = darkColors(
    primary = Color(0xff394739),
    onPrimary = Color.White
)
val LocalHoverColor = compositionLocalOf { Color.Black.copy(0.12f) }
val LocalTabProgressColor = compositionLocalOf { Color.White }
val LocalTabHoverColor = compositionLocalOf { Color.White }

@Composable
fun AppTheme(content: @Composable () -> Unit) = DesktopMaterialTheme(
    colors = colorsDark
) {
    withProviders(
        LocalHoverColor provides MaterialTheme.colors.onSurface.copy(0.12f),
        LocalTabProgressColor provides MaterialTheme.colors.primary.copy(0.5f),
        LocalTabHoverColor provides MaterialTheme.colors.onSurface.copy(0.15f)
    ) { content() }
}

@OptIn(InternalComposeApi::class)
@Composable
inline fun withProviders(vararg values: ProvidedValue<*>, fn: @Composable () -> Unit) {
    currentComposer.startProviders(values)
    fn()
    currentComposer.endProviders()
}