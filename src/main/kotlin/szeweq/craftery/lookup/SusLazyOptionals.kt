package szeweq.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import szeweq.craftery.layout.ThreeLinesItem
import szeweq.craftery.scan.ScanInfo
import java.util.function.Consumer
import java.util.stream.Stream

class SusLazyOptionals: ModLookup<Triple<String, String, String>>("Suspicious LazyOptionals") {
    override val explain = "Craftery detected LazyOptional objects that are not being invalidated properly (it may be done somewhere else)"

    @Composable
    override fun ColumnScope.decorate(item: Triple<String, String, String>) =
        ThreeLinesItem(item, "Name", "Generic class")

    override fun gatherItems(si: ScanInfo): Flow<Triple<String, String, String>> {
        return si.flowLazyOptionals().transform { lo ->
            for (it in lo.warnings) {
                emit(Triple(lo.name, it.key, it.value))
            }
        }
    }
}