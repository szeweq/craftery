package szeweq.craftery.views

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import szeweq.craftery.layout.*
import szeweq.kt.KtUtil
import szeweq.craftery.util.TimeLogManager
import szeweq.craftery.util.ValueHistory
import szeweq.craftery.util.providesMerged
import szeweq.desktopose.core.withProviders
import szeweq.desktopose.hover.hover
import kotlin.math.max

object PerformanceView : View("Performance") {
    private val memUsedHist = ValueHistory()
    private val memTotalHist = ValueHistory()

    private val memoryFlow = flow {
        val rt = Runtime.getRuntime()
        while (true) {
            val t = rt.totalMemory()
            val u = t - rt.freeMemory()
            memUsedHist.add(u)
            memTotalHist.add(t)
            emit( memUsedHist.values to memTotalHist.values)
            delay(1000)
        }
    }

    @Composable
    fun memoryGraph(vsize: Int, values: List<Float>) {
        val prim = MaterialTheme.colors.primary
        val primv = MaterialTheme.colors.primaryVariant
        Canvas(Modifier.fillMaxWidth().requiredHeight(80.dp).padding(top = 6.dp).clip(MaterialTheme.shapes.medium)) {
            drawRect(Color.White, alpha = 0.75F)
            val (w, h) = size
            val spart = w / max(1, vsize - 1)
            val pused = Path().apply {
                moveTo(0F, h)
                for (i in 0 until vsize) {
                    lineTo(spart * i, h - values[i] * h)
                }
                lineTo(w, h)
                close()
            }
            drawPath(pused, prim)
            drawPath(pused, primv, style = Stroke(1f))
            for (i in 0 until vsize) {
                drawLine(Color.DarkGray, Offset(spart * i, 0F), Offset(spart * i, h), alpha = 0.5F)
            }

        }
    }

    @Composable
    override fun content() {
        Column {
            Card(Modifier.padding(12.dp).fillMaxWidth()) {
                val mem by memoryFlow.collectAsState(longArrayOf() to longArrayOf())
                if (mem.first.isEmpty() || mem.second.isEmpty()) {
                    Text("Loading...")
                    return@Card
                }
                val (used, total) = remember(mem) { mem.first.last() to mem.second.last() }
                val txtUsed = remember(mem.first) { KtUtil.lengthInBytes(used) }
                val txtTotal = remember(mem.second) { KtUtil.lengthInBytes(total) }
                val cmax = remember(mem.second) { mem.second.maxOrNull()!! }
                val csz = remember(mem.second) { mem.second.size }
                val vused = remember(mem.first) { mem.first.map { it.toFloat() / cmax } }
                Column(Modifier.padding(8.dp)) {
                    Text("Memory: $txtUsed of $txtTotal")
                    memoryGraph(csz, vused)
                }
            }
            Box {
                val state = rememberLazyListState()
                key(TimeLogManager.lastLog.value) {
                    withProviders(
                        LocalTextStyle providesMerged TextStyle(fontSize = 12.sp)
                    ) {
                        LazyColumn(ModifierMaxSize.padding(horizontal = 12.dp), state = state) {
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
            .hover(shape = MaterialTheme.shapes.medium)
            .padding(4.dp)
        ) {
            Row {
                Text(item.first, Modifier.weight(1.0f))
                Text(TimeLogManager.formatDuration(item.second))
            }
        }
    }
}