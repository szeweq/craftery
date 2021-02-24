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

class DetectCapabilities: ModLookup<Triple<String, String, String>>("Capabilities") {
    override val explain = "List of detected capabilities used by this mod"
    override val itemHeight = 48.dp

    @Composable
    override fun decorate(item: Triple<String, String, String>) {
        Column(Modifier.padding(2.dp)) {
            Text(item.first, fontWeight = FontWeight.Bold)
            Text("Capabilities: " + item.second)
            Text("Inherited from: " + item.third)
        }
    }

    override fun gatherItems(si: ScanInfo): List<Triple<String, String, String>> {
        return si.streamCapabilities().map { c ->
            val f = c.fields + c.supclasses.flatMap { si.getAllCapsFromType(it) }
            val x = if (f.isNotEmpty()) f.joinToString("\n") else "(None provided)"
            val y = c.supclasses.let { if (it.isNotEmpty()) it.joinToString("\n") else "(None provided)" }
            Triple(c.name, x, y)
        }.toList()
    }
}