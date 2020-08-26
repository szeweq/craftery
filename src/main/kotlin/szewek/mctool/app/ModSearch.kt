package szewek.mctool.app

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import szewek.mctool.cfapi.AddonSearch
import szewek.mctool.cfapi.CurseforgeAPI
import tornadofx.*

class ModSearch: View("Search mods") {
    private val modlist: ObservableList<AddonSearch> = FXCollections.observableArrayList()
    private val search = SimpleStringProperty("")
    private val typeId = SimpleIntegerProperty(6)
    private val loading = SimpleBooleanProperty(false)
    private val types = FXCollections.observableArrayList(6, 4471)
    override val root = BorderPane()

    init {
        root.top = hbox(alignment = Pos.CENTER_LEFT) {
            padding = insets(4)
            textfield(search) {
                setOnKeyPressed {
                    if (it.code == KeyCode.ENTER && !search.isEmpty.value) {
                        findMods()
                    }
                }
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
            progressbar {
                padding = insets(4)
                visibleWhen(loading)
            }
        }
        root.center = tableview(modlist) {
            readonlyColumn("Name", AddonSearch::name).pctWidth(20)
            readonlyColumn("Slug", AddonSearch::slug).pctWidth(20)
            readonlyColumn("Download count", AddonSearch::downloadCount).pctWidth(15)
            readonlyColumn("Summary", AddonSearch::summary).remainingWidth()
            onDoubleClick {
                val item = selectedItem
                if (item != null && item.categorySection.packageType == 6) {
                    find<MainView>().openTab(ModInfo(item))
                }
            }
            smartResize()
        }
    }

    private fun findMods() {
        loading.set(true)
        task {
            val a = CurseforgeAPI.findAddons(search.value, typeId.value)
            modlist.setAll(*a)
        } finally {
            loading.set(false)
        }
    }
}