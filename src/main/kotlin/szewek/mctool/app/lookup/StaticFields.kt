package szewek.mctool.app.lookup

import javafx.scene.control.TableView
import szewek.mctool.mcdata.ResourceType
import szewek.mctool.mcdata.ScanInfo
import szewek.mctool.mcdata.fixedDesc
import tornadofx.pctWidth
import tornadofx.readonlyColumn
import tornadofx.remainingWidth
import tornadofx.smartResize
import kotlin.streams.toList

class StaticFields: ModLookup<StaticFields.FieldData>("Static fields: %d") {
    class FieldData(val name: String, val rtype: ResourceType, val from: String, val info: String)

    override val explain = "List of static fields with detected types"

    override fun decorate(tv: TableView<FieldData>) = tv.run {
        readonlyColumn("Name", FieldData::name).pctWidth(15)
        readonlyColumn("Resource type", FieldData::rtype).pctWidth(10)
        readonlyColumn("From", FieldData::from).pctWidth(30)
        readonlyColumn("Info", FieldData::info).remainingWidth()
        smartResize()
    }

    override fun gatherItems(si: ScanInfo): List<FieldData> {
        return si.streamStaticFields().map { (c, v) ->
            val desc = v.fixedDesc
            val rt = si.getResourceType(desc)
            val ift = si.map.getAllInterfaceTypes(desc)
            FieldData(v.name, rt, c.name, "Type: $desc\nInterfaces: ${ift.joinToString()}")
        }.toList()
    }
}