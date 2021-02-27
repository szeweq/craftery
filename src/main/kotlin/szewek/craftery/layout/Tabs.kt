package szewek.craftery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TabsView(views: SnapshotStateList<View>) = Row(Modifier.horizontalScroll(rememberScrollState())) {
    for (v in views) ViewTab(v, MaterialTheme.shapes.small)
}

@Composable
fun ViewTab(v: View, shape: Shape) {
    val hoverBg = MaterialTheme.colors.onSurface.copy(0.1f)
    Box(Modifier.background(if(v.isActive) Color.Black else Color.Transparent, shape).clip(shape)
    ) {
        Row(
            Modifier.clickable(onClick = v::activate).hover(hoverBg, shape).padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(v.title, color = LocalContentColor.current, fontSize = 14.sp, modifier = Modifier.padding(start = 2.dp, end = 4.dp))
            val close = v.close
            if (close != null) {
                Box(
                    Modifier.hover(hoverBg, CircleShape).clip(CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Close",
                        Modifier.size(16.dp).clickable(onClick = close).padding(2.dp),
                        tint = LocalContentColor.current
                    )
                }
            }
        }
    }
}