package szeweq.craftery.lookup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import szeweq.craftery.layout.ModifierMaxSize
import szeweq.craftery.layout.ScrollableColumn
import szeweq.craftery.scan.ScanInfo

abstract class ModLookup<T>(val title: String) {
    val list = mutableStateListOf<T>()

    abstract val explain: String?

    @Composable
    abstract fun ColumnScope.decorate(item: T)

    abstract fun gatherItems(si: ScanInfo): Flow<T>

    @Composable
    fun content() = key(this) {
        if (list.isEmpty()) Box(ModifierMaxSize) {
            Text("This lookup is empty!", Modifier.align(Alignment.Center))
        } else ScrollableColumn {
            items(list) { item -> Column(Modifier.padding(2.dp)) { decorate(item) } }
        }
    }

    suspend fun lazyGather(scope: CoroutineScope, si: ScanInfo) {
        val l = scope.async { gatherItems(si).flowOn(Dispatchers.IO).toList() }
        list.clear()
        list.addAll(l.await())
    }
}
