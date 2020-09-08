package szewek.mctool.app.lookup

import javafx.scene.control.TableView
import szewek.mctool.mcdata.ScanInfo
import tornadofx.*

class ListAllTags: ModLookup<Pair<String, Set<String>>>("Tags: %d") {
    override val explain = "Table displays all found tags"

    override fun decorate(tv: TableView<Pair<String, Set<String>>>) = tv.run {
        readonlyColumn("Name", Pair<String, Set<String>>::first).pctWidth(35)
        readonlyColumn("Values", Pair<String, Set<String>>::second) {
            cellFormat { text = it.joinToString("\n") }
            remainingWidth()
        }
        smartResize()
    }

    override fun gatherItems(si: ScanInfo): List<Pair<String, Set<String>>> {
        return si.tags.entries.map { (k, v) -> k to v }
    }
}