package szeweq.craftery.lookup

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import szeweq.craftery.scan.ScanInfo

abstract class ModLookup<T>(val title: String) {
    val list = mutableStateListOf<T>()

    abstract val explain: String?

    @Composable
    abstract fun ColumnScope.decorate(item: T)

    abstract fun gatherItems(si: ScanInfo): List<T>

    @Composable
    fun content() = key(this) {
        Box(Modifier.fillMaxSize()) {
            if (list.isEmpty()) {
                Text("This lookup is empty!", Modifier.align(Alignment.Center))
            } else {
                val state = rememberLazyListState()
                LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state = state) {
                    items(list) { item -> Column(Modifier.padding(2.dp)) { decorate(item) } }
                }
                VerticalScrollbar(
                    rememberScrollbarAdapter(state),
                    Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                )
            }
        }
    }

    fun lazyGather(si: ScanInfo) {
        val l = gatherItems(si)
        list.clear()
        list.addAll(l)
    }
}