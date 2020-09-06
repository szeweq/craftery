package szewek.mctool.app.lookup

import javafx.scene.control.TableView
import szewek.mctool.mcdata.ScanInfo
import tornadofx.pctWidth
import tornadofx.readonlyColumn
import tornadofx.remainingWidth
import tornadofx.smartResize
import kotlin.streams.toList

class DetectCapabilities: ModLookup<Triple<String, String, String>>("Capabilities: %d") {
    val NONE = "(None provided)"
    override val explain = "List of detected capabilities used by this mod"

    override fun decorate(tv: TableView<Triple<String, String, String>>) = tv.run {
        readonlyColumn("Class", Triple<String, String, String>::first).pctWidth(20)
        readonlyColumn("Capabilities", Triple<String, String, String>::second).pctWidth(30)
        readonlyColumn("Inherited from", Triple<String, String, String>::third).remainingWidth()
        smartResize()
    }

    override fun gatherItems(si: ScanInfo): List<Triple<String, String, String>> {
        return si.streamCapabilities().map { c ->
            val f = c.fields + c.supclasses.flatMap { si.getAllCapsFromType(it) }
            val x = if (f.isNotEmpty()) f.joinToString("\n") else NONE
            val y = c.supclasses.let { if (it.isNotEmpty()) it.joinToString("\n") else NONE }
            Triple(c.name, x, y)
        }.toList()
    }
}