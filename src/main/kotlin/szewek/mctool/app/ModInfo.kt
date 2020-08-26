package szewek.mctool.app

import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import szewek.mctool.cfapi.AddonSearch
import szewek.mctool.cfapi.latest
import tornadofx.*

class ModInfo(private val addon: AddonSearch): View(addon.name) {
    override val root = BorderPane()

    init {
        root.top = BorderPane().apply {
            addClass("page-header")
            left = label(addon.name)
            right = button("Lookup mod code") {
                setOnAction {
                    val lf = addon.latestFiles.latest()
                    if (lf != null) {
                        find<MainView>().openTab(LookupMod(lf))
                    } else {
                        text = "No files found"
                        isDisable = true
                    }
                }
            }
        }
        root.center = gridpane {
            addClass("page-info")
            hgap = 4.0
            vgap = 4.0
            f("Name", addon::name)
            f("Slug", addon::slug)
            f("Summary", addon::summary)
            af("Download count", addon::downloadCount)
            row { hyperlink(addon.websiteUrl) {
                setOnAction { hostServices.showDocument(addon.websiteUrl) }
                gridpaneConstraints { columnSpan = 2 }
            } }
        }
    }

    private inline fun GridPane.f(name: String, crossinline fn: () -> String) = row {
        add(Label(name).also { it.addClass("bolder") })
        add(Label(fn()))
    }
    private inline fun GridPane.af(name: String, crossinline fn: () -> Any?) = row {
        add(Label(name).also { it.addClass("bolder") })
        add(Label(fn().toString()))
    }
}