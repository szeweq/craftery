package szewek.craftery.views

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szewek.craftery.layout.LocalHoverColor
import szewek.craftery.layout.View
import szewek.craftery.layout.hover
import szewek.craftery.layout.withProviders
import szewek.craftery.util.TimeLogManager

object TimeLogViewer : View("Time logs") {

    @Composable
    override fun content() {
        Box {
            val state = rememberLazyListState()
            key(TimeLogManager.lastLog.value) {
                withProviders(
                    LocalTextStyle provides TextStyle(fontSize = 12.sp)
                ) {
                    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp), state = state) {
                        val avgs = TimeLogManager.averages()
                        if (avgs.isEmpty()) item {
                            Box(Modifier.fillMaxWidth().height(64.dp), contentAlignment = Alignment.Center) {
                                Text("Empty")
                            }
                        } else items(avgs, { it.first }) {
                            itemBox(it)
                        }
                    }
                }
            }
            VerticalScrollbar(
                rememberScrollbarAdapter(state),
                Modifier.align(Alignment.CenterEnd).fillMaxHeight()
            )
        }
    }

    @Composable
    private fun itemBox(item: Pair<String, Long>) {
        Box(Modifier
            .hover(LocalHoverColor.current, shape = MaterialTheme.shapes.medium)
            .padding(4.dp)
        ) {
            Row {
                Text(item.first, Modifier.weight(1.0f))
                Text(TimeLogManager.formatDuration(item.second))
            }

        }
    }
}