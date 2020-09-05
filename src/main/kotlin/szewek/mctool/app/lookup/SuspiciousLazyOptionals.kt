package szewek.mctool.app.lookup

import javafx.scene.control.TableView
import szewek.mctool.mcdata.ScanInfo
import tornadofx.pctWidth
import tornadofx.readonlyColumn
import tornadofx.remainingWidth
import tornadofx.smartResize
import kotlin.streams.toList

class SuspiciousLazyOptionals: ModLookup<Triple<String, String, String>>("Suspicious LazyOptionals: %d") {
    override val explain = "MCTool detected LazyOptional objects that are not being invalidated properly (it may be done somewhere else)"

    override fun TableView<Triple<String, String, String>>.decorate() {
        readonlyColumn("Class", Triple<String, String, String>::first).pctWidth(35)
        readonlyColumn("Name", Triple<String, String, String>::second).pctWidth(15)
        readonlyColumn("Generic class", Triple<String, String, String>::third).remainingWidth()
        smartResize()
    }

    override fun gatherItems(si: ScanInfo): List<Triple<String, String, String>> {
        return si.streamLazyOptionals().flatMap { lo ->
            lo.warnings.stream().map { Triple(lo.name, it.first, it.second) }
        }.toList()
    }
}