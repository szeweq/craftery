package szeweq.craftery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import szeweq.craftery.util.providesFrom
import szeweq.craftery.util.providesMerged
import szeweq.desktopose.core.withProviders
import szeweq.desktopose.hover.LocalHoverColor
import szeweq.desktopose.hover.hover
import szeweq.desktopose.progress.LinearIndicator

val tabShape = RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp)

private fun Modifier.scrollAction(scope: CoroutineScope, scrollState: LazyListState, value: Float) =
    clickable { scope.launch { scrollState.animateScrollBy(value) } }

private fun LazyListState.scopeScrollToItem(scope: CoroutineScope, index: Int) =
    scope.launch { animateScrollToItem(index) }

@Composable
fun TabsView(modifier: Modifier = Modifier, views: SnapshotStateList<View>) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Box(modifier) {
        Icon(
            Icons.Default.ArrowBack,
            "Previous tabs",
            Modifier.size(16.dp).align(Alignment.CenterStart).scrollAction(scope, lazyListState, -200f)
        )
        withProviders(
            LocalTextStyle providesMerged MaterialTheme.typography.caption,
            LocalHoverColor providesFrom LocalTabHoverColor,
            LocalContentAlpha provides 1f
        ) {
            LazyRow(
                Modifier.matchParentSize().padding(horizontal = 20.dp),
                lazyListState,
                verticalAlignment = Alignment.Bottom
            ) {
                itemsIndexed(views) { i, v ->
                    ViewTab(v) { lazyListState.scopeScrollToItem(scope, i) }
                }
                scope.launch {
                    val ix = views.indexOfFirst(View::isActive)
                    if (ix != -1) lazyListState.scrollToItem(ix)
                }
            }
        }
        Icon(
            Icons.Default.ArrowForward,
            "Next tabs",
            Modifier.size(16.dp).align(Alignment.CenterEnd).scrollAction(scope, lazyListState, 200f)
        )

    }
}

@Composable
fun ViewTab(v: View, activate: () -> Unit) {
    Box(
        Modifier
            .background(if(v.isActive) MaterialTheme.colors.background else Color.Transparent, tabShape)
            .clip(tabShape),
        propagateMinConstraints = true
    ) {
        if (v.progress.isActive) LinearIndicator(
            v.progress,
            Modifier.matchParentSize().padding(bottom = 24.dp),
            LocalTabProgressColor.current
        )
        Row(
            Modifier
                .clickableNumbered(1, 2) {
                    if (it == 2) v.tryClose()
                    v.activate()
                    activate()
                }
                .hover(shape = tabShape)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(v.title.value, Modifier.padding(start = 2.dp, end = 4.dp))
            val close = v.close
            if (close != null) {
                CloseButton(close)
            }
        }
    }
}

private val ModifierCloseButton = Modifier
    .size(16.dp)
    .hover(shape = CircleShape)
    .clip(CircleShape)
    .padding(2.dp)

@Composable
private fun CloseButton(action: () -> Unit) = Icon(
    Icons.Default.Close,
    "Close",
    ModifierCloseButton.clickable(onClick = action)
)
