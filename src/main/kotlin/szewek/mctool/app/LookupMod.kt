package szewek.mctool.app

import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane
import szewek.mctool.cfapi.AddonFile
import szewek.mctool.util.Downloader
import szewek.mctool.util.Scanner
import tornadofx.*

class LookupMod(private val file: AddonFile): View("Lookup: ${file.fileName}") {
    private val fieldList: ObservableList<Triple<String, String, String>> = FXCollections.observableArrayList()
    private val progressBar = progressbar {
        visibleWhen(Bindings.lessThan(progressProperty(), 1))
    }
    override val root = BorderPane()

    init {
        root.top = progressBar.apply { prefWidthProperty().bind(root.widthProperty()) }
        root.center = tableview(fieldList) {
            readonlyColumn("Name", Triple<String, String, String>::first).pctWidth(15)
            readonlyColumn("From", Triple<String, String, String>::second).pctWidth(30)
            readonlyColumn("Info", Triple<String, String, String>::third).remainingWidth()
            smartResize()
        }
        lookupFields()
    }

    private fun lookupFields() {
        fieldList.clear()
        val t = task {
            updateProgress(0, 3)
            val z = Downloader.downloadZip(file.downloadUrl)
            updateProgress(1, 3)
            val si = Scanner.scanArchive(z)
            updateProgress(2, 3)
            val cx = si.caps.values.map { c ->
                val f = c.fields + c.supclasses.flatMap { si.getAllCapsFromType(it) }
                val x = if (f.isNotEmpty()) f.joinToString() else "Inherited from classes: " + c.supclasses.joinToString()
                Triple("[CAPABILITIES]", c.name, "Caps: $x")
            }
            val x = si.classes.values.flatMap { it.fields.values.map { v ->
                Triple(v.name, it.name, "Type: ${v.type}")
            } }
            fieldList += cx + x
            updateProgress(3, 3)
        }
        progressBar.progressProperty().cleanBind(t.progressProperty())
    }
}