package szewek.craftery.lookup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import szewek.craftery.mcdata.ScanInfo
import kotlin.streams.toList

class SusLazyOptionals: ModLookup<Triple<String, String, String>>("Suspicious LazyOptionals") {
    override val explain = "Craftery detected LazyOptional objects that are not being invalidated properly (it may be done somewhere else)"
    override val itemHeight = 48.dp

    @Composable
    override fun decorate(item: Triple<String, String, String>) {
        Column(Modifier.padding(2.dp)) {
            Text(item.first, fontWeight = FontWeight.Bold)
            Text("Name: " + item.second)
            Text("Generic class: " + item.third)
        }
    }

    override fun gatherItems(si: ScanInfo): List<Triple<String, String, String>> {
        return si.streamLazyOptionals().flatMap { lo ->
            lo.warnings.stream().map { Triple(lo.name, it.first, it.second) }
        }.toList()
    }
}