package szewek.craftery.lookup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import szewek.craftery.mcdata.ScanInfo

abstract class ModLookup<T>(val title: String) {
    val list = mutableStateListOf<T>()

    abstract val explain: String?
    abstract val itemHeight: Dp

    @Composable
    abstract fun decorate(item: T)

    abstract fun gatherItems(si: ScanInfo): List<T>

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun content() {
        Box {
            val state = rememberLazyListState()
            LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state = state) {
                items(list) { item -> decorate(item) }
            }
            VerticalScrollbar(
                rememberScrollbarAdapter(state, list.size, itemHeight),
                Modifier.align(Alignment.CenterEnd).fillMaxHeight()
            )
        }
    }

    fun lazyGather(si: ScanInfo) {
        val l = gatherItems(si)
        list.clear()
        list.addAll(l)
    }
}