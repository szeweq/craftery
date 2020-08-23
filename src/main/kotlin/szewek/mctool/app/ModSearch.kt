package szewek.mctool.app

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.mctool.cfapi.AddonSearch
import szewek.mctool.cfapi.CurseforgeAPI
import tornadofx.*

class ModSearch: Fragment("Search mods") {
    private val modlist: ObservableList<AddonSearch> = FXCollections.observableArrayList()
    private val search = SimpleStringProperty()
    override val root = borderpane {
        top = hbox {
            padding = insets(4)
            textfield(search)
            button("Search") {
                action {
                    if (!search.isEmpty.value) {
                        findMods(search.value)
                    }
                }
            }
        }
        center = tableview(modlist) {
            readonlyColumn("Name", AddonSearch::name).pctWidth(20)
            readonlyColumn("Slug", AddonSearch::slug).pctWidth(20)
            readonlyColumn("Summary", AddonSearch::summary).remainingWidth()
            onDoubleClick {
                val item = selectedItem
                if (item != null) {
                    find<MainView>().tabPane.tab(LookupModFields(item)) {
                        text = "Fields: ${item.name}"
                        select()
                    }
                }
            }
            smartResize()
        }
    }

    private fun findMods(query: String) {
        GlobalScope.launch {
            val a = CurseforgeAPI.findAddons(query, 6)
            modlist.setAll(*a)
        }
    }
}