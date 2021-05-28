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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val tabShape = RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp)
val LocalTabProgressColor = staticCompositionLocalOf { Color.White }

@Composable
fun TabsView(modifier: Modifier = Modifier, views: SnapshotStateList<View>) = Row(
    modifier.horizontalScroll(rememberScrollState()),
    verticalAlignment = Alignment.Bottom
) {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(fontSize = 14.sp),
        LocalTabProgressColor provides MaterialTheme.colors.primary.copy(alpha = 0.5f)
    ) { for (v in views) ViewTab(v) }
}

@Composable
fun ViewTab(v: View) {
    val hoverBg = MaterialTheme.colors.onSurface.copy(0.1f)
    Box(
        Modifier.background(if(v.isActive) MaterialTheme.colors.background else Color.Transparent, tabShape).clip(tabShape),
        propagateMinConstraints = true
    ) {
        if (v.progress.isActive()) LinearIndicator(v.progress, Modifier.matchParentSize(), LocalTabProgressColor.current)
        Row(
            Modifier.clickable(onClick = v::activate).hover(hoverBg, tabShape).padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(v.title.value, color = LocalContentColor.current, modifier = Modifier.padding(start = 2.dp, end = 4.dp))
            val close = v.close
            if (close != null) {
                CloseButton(hoverBg, close)
            }
        }
    }
}

@Composable
private fun CloseButton(hoverBg: Color, action: () -> Unit) {
    Box(
        Modifier.hover(hoverBg, CircleShape).clip(CircleShape)
    ) {
        Icon(
            Icons.Default.Close,
            "Close",
            Modifier.size(16.dp).clickable(onClick = action).padding(2.dp),
            tint = LocalContentColor.current
        )
    }
}