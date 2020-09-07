package szewek.mctool.app

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import szewek.mctool.app.task.TaskManager
import szewek.mctool.cfapi.AddonSearch
import szewek.mctool.cfapi.CurseforgeAPI
import tornadofx.*

class ModSearch: View("Search mods") {
    private val modlist: ObservableList<AddonSearch> = FXCollections.observableArrayList()
    private val search = SimpleStringProperty("")
    private val typeId = SimpleIntegerProperty(6)
    private val types = FXCollections.observableArrayList(6, 4471)
    override val root = BorderPane()

    init {
        root.top = HBox().apply {
            alignment = Pos.CENTER_LEFT
            padding = insets(4)
            textfield(search) {
                promptText = "Search..."
                setOnKeyPressed {
                    if (it.code == KeyCode.ENTER && !search.isEmpty.value) {
                        findMods()
                    }
                }
                hgrow = Priority.ALWAYS
            }
            combobox(typeId, types) {
                cellFormat {
                    text = when (it) {
                        6 -> "Mod"
                        4471 -> "Modpack"
                        else -> "UNKNOWN"
                    }
                }
            }
            button("Search") {
                disableWhen(search.isEmpty)
                setOnAction {
                    if (!search.isEmpty.value) {
                        findMods()
                    }
                }
            }
        }
        root.center = tableview(modlist) {
            readonlyColumn("Name", AddonSearch::name).pctWidth(20)
            readonlyColumn("Slug", AddonSearch::slug).pctWidth(20)
            readonlyColumn("Download count", AddonSearch::downloadCount).pctWidth(15)
            readonlyColumn("Summary", AddonSearch::summary).remainingWidth()
            onDoubleClick {
                val item = selectedItem
                if (item != null) {
                    find<MainView>().openTab(AddonInfoView(item))
                }
            }
            smartResize()
        }
    }

    private fun findMods() {
        val t = task {
            val s = search.value
            updateMessage("Searching $s...")
            val a = CurseforgeAPI.findAddons(search.value, typeId.value)
            modlist.setAll(*a)
        }
        TaskManager.addTask(t)
    }
}