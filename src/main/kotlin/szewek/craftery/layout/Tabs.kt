package szewek.craftery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TabsView(modifier: Modifier = Modifier, views: SnapshotStateList<View>) = Row(
    modifier.horizontalScroll(rememberScrollState()),
    verticalAlignment = Alignment.CenterVertically
) {
    ProvideTextStyle(TextStyle(fontSize = 14.sp)) {
        for (v in views) ViewTab(v, MaterialTheme.shapes.small)
    }
}

@Composable
fun ViewTab(v: View, shape: Shape) {
    val hoverBg = MaterialTheme.colors.onSurface.copy(0.1f)
    Box(
        Modifier.background(if(v.isActive) Color.Black else Color.Transparent, shape).clip(shape),
        propagateMinConstraints = true
    ) {
        Row(
            Modifier.clickable(onClick = v::activate).hover(hoverBg, shape).padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(v.title.value, color = LocalContentColor.current, modifier = Modifier.padding(start = 2.dp, end = 4.dp))
            val close = v.close
            if (close != null) {
                CloseButton(hoverBg, close)
            }
        }
        if (v.progress.isActive()) LinearIndicator(v.progress, Modifier.matchParentSize().padding(top = 24.dp))
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