package szewek.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import szewek.craftery.mcdata.ResourceType
import szewek.craftery.mcdata.ScanInfo
import szewek.craftery.mcdata.Scanner
import szewek.craftery.mcdata.fixedDesc

class StaticFields: ModLookup<StaticFields.FieldData>("Static fields") {
    class FieldData(val name: String, val rtype: ResourceType, val from: String, val info: String)

    override val explain = "List of static fields with detected types"
    override val itemHeight = 48.dp

    @Composable
    override fun ColumnScope.decorate(item: FieldData) {
        Text(item.name, fontWeight = FontWeight.Bold)
        Text("Resource type: " + item.rtype)
        Text("From: " + item.from)
        Text("Info:" + item.info)
    }

    override fun gatherItems(si: ScanInfo): List<FieldData> {
        return si.streamStaticFields().map { (c, v) ->
            val desc = v.fixedDesc
            val rt = si.getResourceType(desc)
            val ift = si.map.getAllInterfaceTypes(desc)
            val sig = if (v.signature != null) "\nSignature: ${Scanner.genericFromSignature(v.signature)}" else ""
            FieldData(
                v.name,
                rt,
                c.name,
                "Type: $desc\nInterfaces: ${ift.joinToString()}$sig"
            )
        }.toList()
    }
}