package szewek.mctool.app

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import szewek.mctool.cfapi.AddonFile
import szewek.mctool.util.Downloader
import szewek.mctool.util.Scanner
import tornadofx.*

class LookupMod(private val file: AddonFile): View("Lookup: ${file.fileName}") {
    private val capList: ObservableList<Triple<String, String, String>> = FXCollections.observableArrayList()
    private val fieldList: ObservableList<Triple<String, String, String>> = FXCollections.observableArrayList()
    override val root = LoaderPane()

    init {
        root.apply {
            accordion(
                titledpane(capList.sizeProperty.asString("Capabilities (%d)")) {
                    isExpanded = false
                    tableview(capList) {
                        readonlyColumn("Class", Triple<String, String, String>::first).pctWidth(20)
                        readonlyColumn("Capabilities", Triple<String, String, String>::second).pctWidth(30)
                        readonlyColumn("Inherited from", Triple<String, String, String>::third).remainingWidth()
                        smartResize()
                    }
                },
                titledpane(fieldList.sizeProperty.asString("Fields (%d)")) {
                    isExpanded = false
                    tableview(fieldList) {
                        readonlyColumn("Name", Triple<String, String, String>::first).pctWidth(15)
                        readonlyColumn("From", Triple<String, String, String>::second).pctWidth(30)
                        readonlyColumn("Info", Triple<String, String, String>::third).remainingWidth()
                        smartResize()
                    }
                }
            )
        }
        lookupFields()
    }

    private fun lookupFields() {
        root.launchTask {
            updateMessage("Downloading file...")
            updateProgress(0, 3)
            val z = Downloader.downloadZip(file.downloadUrl)
            updateMessage("Scanning classes...")
            updateProgress(1, 3)
            val si = Scanner.scanArchive(z)
            updateMessage("Gathering results...")
            updateProgress(2, 3)
            val cx = si.caps.values.map { c ->
                val f = c.fields + c.supclasses.flatMap { si.getAllCapsFromType(it) }
                val x = if (f.isNotEmpty()) f.joinToString() else "Inherited from classes: " + c.supclasses.joinToString()
                Triple("[CAPABILITIES]", c.name, "Caps: $x")
            }
            val x = si.classes.values.flatMap { it.fields.values.map { v ->
                val rt = si.getResourceType(v.type)
                val ift = si.getAllInterfaceTypes(v.type)
                Triple(v.name, it.name, "Type: ${v.type}\nInterfaces: ${ift.joinToString()}\nResource type: ${rt ?: "NONE"}")
            } }
            updateProgress(3, 3)
            runLater {
                capList.setAll(cx)
                fieldList.setAll(x)
            }

        }
    }
}