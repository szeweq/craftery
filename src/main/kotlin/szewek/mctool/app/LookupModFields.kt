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

class LookupModFields(private val addon: AddonSearch): Fragment(addon.name) {
    private val fieldList: ObservableList<Scanner.FieldInfo> = FXCollections.observableArrayList()
    private val progress = SimpleFloatProperty()
    override val root = BorderPane()

    init {
        root.apply {
            top = progressbar(progress)
            center = tableview(fieldList) {
                readonlyColumn("Name", Scanner.FieldInfo::name).pctWidth(20)
                readonlyColumn("Type", Scanner.FieldInfo::type).pctWidth(60)
                readonlyColumn("From", Scanner.FieldInfo::container).remainingWidth()
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
                var ze = z.nextEntry
                while (ze != null) {
                    if (!ze.isDirectory && ze.name.endsWith(".class")) {
                        val l = Scanner.scanClass(ze.name, z.readBytes())
                        fieldList += l
                    }
                    ze = z.nextEntry
                }
            }
            progress.value = 1f
        }
    }
}