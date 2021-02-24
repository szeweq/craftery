package szewek.craftery.layout

import androidx.compose.foundation.ScrollbarStyle
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