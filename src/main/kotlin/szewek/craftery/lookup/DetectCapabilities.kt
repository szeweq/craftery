package szewek.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import szewek.craftery.layout.ThreeLinesItem
import szewek.craftery.scan.ScanInfo

class DetectCapabilities: ModLookup<Triple<String, String, String>>("Capabilities") {
    override val explain = "List of detected capabilities used by this mod"
    override val itemHeight = 48.dp

    @Composable
    override fun ColumnScope.decorate(item: Triple<String, String, String>) =
        ThreeLinesItem(item, "Capabilities", "Inherited from")

    override fun gatherItems(si: ScanInfo): List<Triple<String, String, String>> {
        return si.streamCapabilities().map { c ->
            val f = c.fields + c.supclasses.flatMap { si.getAllCapsFromType(it) }
            val x = if (f.isNotEmpty()) f.joinToString("\n") else "(None provided)"
            val y = c.supclasses.let { if (it.isNotEmpty()) it.joinToString("\n") else "(None provided)" }
            Triple(c.name, x, y)
        }.toList()
    }
}