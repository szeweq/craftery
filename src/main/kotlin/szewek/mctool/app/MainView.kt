package szewek.mctool.app

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TabPane
import javafx.scene.control.TableView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.mctool.cfapi.AddonSearch
import szewek.mctool.cfapi.CurseforgeAPI
import szewek.mctool.cfapi.latest
import szewek.mctool.util.Downloader
import szewek.mctool.util.Scanner
import tornadofx.*


class MainView: View() {
    var tabPane: TabPane by singleAssign()
    override val root = borderpane {
        title = "MCTool"
        center = stackpane {
            tabpane {
                tabPane = this
                visibleWhen { Bindings.isNotEmpty(tabs) }
                tab<ModSearch>()
            }
            text("EMPTY") {
                visibleWhen { Bindings.isEmpty(tabPane.tabs) }
            }
        }
    }

    init {
        primaryStage.apply {
            title = "MCTool"
            width = 800.0
            height = 480.0
        }
    }

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

    class LookupModFields(private val addon: AddonSearch): Fragment(addon.name) {
        private var tableView: TableView<Scanner.FieldInfo> by singleAssign()
        private val fieldList: ObservableList<Scanner.FieldInfo> = FXCollections.observableArrayList()
        private val progress = SimpleFloatProperty()
        override val root = borderpane {
            top = progressbar(progress)
            center = tableview(fieldList) {
                tableView = this
                readonlyColumn("Name", Scanner.FieldInfo::name).pctWidth(20)
                readonlyColumn("Type", Scanner.FieldInfo::type).pctWidth(60)
                readonlyColumn("From", Scanner.FieldInfo::container).remainingWidth()
                smartResize()
            }
        }

        init {
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
}