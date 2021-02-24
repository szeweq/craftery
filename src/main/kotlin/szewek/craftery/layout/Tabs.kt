package szewek.craftery.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TabsView(views: SnapshotStateList<View>) = Row(Modifier.horizontalScroll(rememberScrollState())) {
    for (v in views) ViewTab(v)
}

@Composable
fun ViewTab(v: View) = Surface(
    color = if(v.isActive) Color.Black else Color.Transparent,
    shape = MaterialTheme.shapes.small
) {
    Row(Modifier.clickable(onClick = v::activate).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(v.title, color = LocalContentColor.current, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))
        val close = v.close
        if (close != null)
            Icon(Icons.Default.Close, "Close", Modifier.size(24.dp).padding(4.dp).clickable(onClick = close), tint = LocalContentColor.current)
    }
}