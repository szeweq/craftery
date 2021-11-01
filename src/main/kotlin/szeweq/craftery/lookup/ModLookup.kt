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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import szeweq.craftery.layout.ModifierMaxSize
import szeweq.craftery.layout.ScrollableColumn
import szeweq.craftery.scan.ScanInfo
import java.util.stream.Stream

abstract class ModLookup<T>(val title: String) {
    val list = mutableStateListOf<T>()

    abstract val explain: String?

    @Composable
    abstract fun ColumnScope.decorate(item: T)

    abstract fun gatherItems(si: ScanInfo): Stream<T>

    @Composable
    fun content() = key(this) {
        if (list.isEmpty()) Box(ModifierMaxSize) {
            Text("This lookup is empty!", Modifier.align(Alignment.Center))
        } else ScrollableColumn {
            items(list) { item -> Column(Modifier.padding(2.dp)) { decorate(item) } }
        }
    }

    suspend fun lazyGather(scope: CoroutineScope, si: ScanInfo) {
        val l = scope.async { gatherItems(si).toList() }
        list.clear()
        list.addAll(l.await())
    }
}
