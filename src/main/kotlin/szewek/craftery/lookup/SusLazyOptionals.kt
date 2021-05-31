package szewek.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import szewek.craftery.layout.ThreeLinesItem
import szewek.craftery.mcdata.ScanInfo

class SusLazyOptionals: ModLookup<Triple<String, String, String>>("Suspicious LazyOptionals") {
    override val explain = "Craftery detected LazyOptional objects that are not being invalidated properly (it may be done somewhere else)"
    override val itemHeight = 48.dp

    @Composable
    override fun ColumnScope.decorate(item: Triple<String, String, String>) =
        ThreeLinesItem(item, "Name", "Generic class")

    override fun gatherItems(si: ScanInfo): List<Triple<String, String, String>> {
        return si.streamLazyOptionals().flatMap { lo ->
            lo.warnings.stream().map { Triple(lo.name, it.first, it.second) }
        }.toList()
    }
}