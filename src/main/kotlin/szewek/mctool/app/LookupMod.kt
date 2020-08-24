package szewek.mctool.app

import javafx.beans.property.SimpleFloatProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.mctool.cfapi.AddonSearch
import szewek.mctool.cfapi.latest
import szewek.mctool.util.Downloader
import szewek.mctool.util.Scanner
import tornadofx.*

class LookupMod(private val addon: AddonSearch): Fragment(addon.name) {
    private val fieldList: ObservableList<Triple<String, String, String>> = FXCollections.observableArrayList()
    private val progress = SimpleFloatProperty()
    override val root = BorderPane()

    init {
        root.apply {
            top = progressbar(progress)
            center = tableview(fieldList) {
                readonlyColumn("Name", Triple<String, String, String>::first).pctWidth(15)
                readonlyColumn("From", Triple<String, String, String>::second).pctWidth(30)
                readonlyColumn("Info", Triple<String, String, String>::third).remainingWidth()
                smartResize()
            }
        }
        lookupFields()
    }

    private fun lookupFields() {
        GlobalScope.launch {
            fieldList.clear()
            progress.value = 0f
            val lf = addon.latestFiles.latest()
            if (lf != null) {
                progress.value = 0.25f
                val z = Downloader.downloadZip(lf.downloadUrl)
                progress.value = 0.5f
                val si = Scanner.scanArchive(z)
                val cx = si.caps.values.map { c ->
                    val f = c.fields + c.supclasses.flatMap { si.getAllCapsFromType(it) }
                    val x = if (f.isNotEmpty()) f.joinToString() else "Inherited from classes: " + c.supclasses.joinToString()
                    Triple("[CAPABILITIES]", c.name, "Caps: $x")
                }
                val x = si.classes.values.flatMap { it.fields.values.map { v ->
                    Triple(v.name, it.name, "Type: ${v.type}")
                } }
                fieldList += cx + x
            }
            progress.value = 1f
        }
    }
}