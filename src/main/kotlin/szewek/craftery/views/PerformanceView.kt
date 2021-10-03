package szewek.craftery.views

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import szewek.craftery.layout.LocalHoverColor
import szewek.craftery.layout.View
import szewek.craftery.layout.hover
import szewek.craftery.layout.withProviders
import szewek.craftery.util.TimeLogManager

object PerformanceView : View("Performance") {

    private fun memoryFlow() = flow {
        val rt = Runtime.getRuntime()
        while (true) {
            val t = rt.totalMemory()
            emit(t - rt.freeMemory() to t)
            delay(1000)
        }
    }

    @Composable
    override fun content() {
        Column {
            Card(Modifier.padding(12.dp).fillMaxWidth()) {
                val mem by remember { memoryFlow() }.collectAsState(0L to 0L)
                Column(Modifier.padding(8.dp)) {
                    Text("Memory: %.2f of %.2f kB".format(mem.first.toFloat() / 1024, mem.second.toFloat() / 1024))
                    LinearProgressIndicator((mem.second - mem.first).toFloat() / mem.second)
                }
            }
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