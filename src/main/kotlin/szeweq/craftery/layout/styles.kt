package szeweq.craftery.layout

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val colorsDark = darkColors(
    primary = Color(0xff388e3c),
    onPrimary = Color(0xff002200)
)
val LocalHoverColor = compositionLocalOf { Color.Black.copy(0.12f) }
val LocalTabProgressColor = compositionLocalOf { Color.White }
val LocalTabHoverColor = compositionLocalOf { Color.White }

val appTypography = Typography(
    h5 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    body1 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    body2 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    button = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 0.5.sp)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) = MaterialTheme(
    colors = colorsDark,
    typography = appTypography
) {
    val scrollbar = ScrollbarStyle(
        minimalHeight = 16.dp,
        thickness = 8.dp,
        shape = MaterialTheme.shapes.small,
        hoverDurationMillis = 300,
        unhoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
        hoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.50f)
    )
    withProviders(
        LocalHoverColor provides MaterialTheme.colors.onSurface.copy(0.12f),
        LocalTabProgressColor provides MaterialTheme.colors.primary.copy(0.5f),
        LocalTabHoverColor provides MaterialTheme.colors.onSurface.copy(0.15f),
        LocalScrollbarStyle provides scrollbar,
        fn = content
    )
}

@OptIn(InternalComposeApi::class)
@Composable
inline fun withProviders(vararg values: ProvidedValue<*>, fn: @Composable () -> Unit) {
    currentComposer.startProviders(values)
    fn()
    currentComposer.endProviders()
}