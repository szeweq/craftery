package szewek.mctool.app.lookup

import javafx.scene.control.TableView
import szewek.mctool.mcdata.DataResourceType
import szewek.mctool.mcdata.ScanInfo
import tornadofx.pctWidth
import tornadofx.readonlyColumn
import tornadofx.remainingWidth
import tornadofx.smartResize

class ListResourceData: ModLookup<ListResourceData.DataFields>("Resources: %d") {
    class DataFields(val name: String, val drtype: DataResourceType, val namespace: String, val info: String)

    override val explain: String? = null

    override fun decorate(tv: TableView<DataFields>) = tv.run {
        readonlyColumn("Path", DataFields::name).pctWidth(30)
        readonlyColumn("Type", DataFields::drtype).pctWidth(15)
        readonlyColumn("Namespace", DataFields::namespace).pctWidth(15)
        readonlyColumn("Info", DataFields::info).remainingWidth()
        smartResize()
    }

    override fun gatherItems(si: ScanInfo): List<DataFields> {
        return si.res.values.map {
            val info = if (it.details.isEmpty()) "(None)" else it.details.entries.joinToString("\n") { (k, v) -> "$k: $v" }
            DataFields(it.name, it.type, it.namespace, info)
        }
    }
}