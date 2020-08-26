package szewek.mctool.app

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import szewek.mctool.cfapi.AddonFile
import szewek.mctool.util.Downloader
import szewek.mctool.util.ResourceType
import szewek.mctool.util.Scanner
import tornadofx.*

class LookupMod(private val file: AddonFile): View("Lookup: ${file.fileName}") {
    private val capList: ObservableList<Triple<String, String, String>> = FXCollections.observableArrayList()
    private val fieldList: ObservableList<FieldData> = FXCollections.observableArrayList()
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
                        readonlyColumn("Name", FieldData::name).pctWidth(15)
                        readonlyColumn("Resource type", FieldData::rtype).pctWidth(10)
                        readonlyColumn("From", FieldData::from).pctWidth(30)
                        readonlyColumn("Info", FieldData::info).remainingWidth()
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
                val x = if (f.isNotEmpty()) f.joinToString("\n") else "(None provided)"
                val y = c.supclasses.let { if (it.isNotEmpty()) it.joinToString("\n") else "(None provided)" }
                Triple(c.name, x, y)
            }
            val x = si.classes.values.flatMap { it.fields.values.map { v ->
                val rt = si.getResourceType(v.type)
                val ift = si.getAllInterfaceTypes(v.type)
                FieldData(v.name, rt, it.name, "Type: ${v.type}\nInterfaces: ${ift.joinToString()}")
            } }
            updateProgress(3, 3)
            runLater {
                capList.setAll(cx)
                fieldList.setAll(x)
            }

        }
    }

    internal class FieldData(val name: String, val rtype: ResourceType, val from: String, val info: String)
}