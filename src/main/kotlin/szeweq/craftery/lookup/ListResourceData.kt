package szeweq.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import szeweq.craftery.mcdata.DataResourceType
import szeweq.craftery.scan.ScanInfo

class ListResourceData: ModLookup<ListResourceData.DataFields>("Resources") {
    class DataFields(val name: String, val drtype: DataResourceType, val namespace: String, val info: String)

    override val explain: String? = null

    @Composable
    override fun ColumnScope.decorate(item: DataFields) {
        Text(item.name, fontWeight = FontWeight.Bold)
        Text("Type: " + item.drtype)
        Text("Namespace: " + item.namespace)
        Text("Info: " + item.info)
    }

    override fun gatherItems(si: ScanInfo): List<DataFields> {
        return si.res.values.map {
            val info = if (it.details.isEmpty()) "(None)" else it.details.entries.joinToString("\n") { (k, v) -> "$k: $v" }
            DataFields(it.name, it.type, it.namespace, info)
        }
    }
}