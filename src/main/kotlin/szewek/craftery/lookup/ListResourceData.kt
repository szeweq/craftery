package szewek.craftery.lookup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import szewek.craftery.mcdata.DataResourceType
import szewek.craftery.mcdata.ScanInfo

class ListResourceData: ModLookup<ListResourceData.DataFields>("Resources") {
    class DataFields(val name: String, val drtype: DataResourceType, val namespace: String, val info: String)

    override val explain: String? = null
    override val itemHeight = 48.dp

    @Composable
    override fun decorate(item: DataFields) {
        Column(Modifier.padding(2.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold)
            Text("Type: " + item.drtype)
            Text("Namespace: " + item.namespace)
            Text("Info: " + item.info)
        }
    }

    override fun gatherItems(si: ScanInfo): List<DataFields> {
        return si.res.values.map {
            val info = if (it.details.isEmpty()) "(None)" else it.details.entries.joinToString("\n") { (k, v) -> "$k: $v" }
            DataFields(it.name, it.type, it.namespace, info)
        }
    }
}