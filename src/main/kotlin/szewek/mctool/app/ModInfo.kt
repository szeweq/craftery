package szewek.mctool.app

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.*
import szewek.mctool.cfapi.AddonFile
import szewek.mctool.cfapi.AddonSearch
import szewek.mctool.cfapi.latest
import szewek.mctool.util.FileLoader
import tornadofx.*

class ModInfo(private val addon: AddonSearch): View(addon.name) {
    override val root = BorderPane()

    init {
        root.top = HBox().apply {
            spacing = 4.0
            alignment = Pos.CENTER_LEFT
            addClass("page-header")
            Label(addon.name).attachTo(this) {
                maxWidth = Double.MAX_VALUE
                hgrow = Priority.ALWAYS
            }
            button("Lookup mod code") {
                setOnAction {
                    val lf = addon.latestFiles.latest()
                    if (lf != null) {
                        find<MainView>().openTab(LookupMod(lf.fileName, FileLoader.fromURL(lf.downloadUrl)))
                    } else {
                        text = "No files found"
                        isDisable = true
                    }
                }
            }
        }
        root.center = gridpane {
            fitToWidth(root)
            addClass("page-info")
            columnConstraints.setAll(ColumnConstraints(), ColumnConstraints(-1.0, -1.0, Double.MAX_VALUE).also { hgrow = Priority.ALWAYS })
            hgap = 4.0
            vgap = 4.0
            f("Name", addon::name)
            f("Slug", addon::slug)
            f("Summary", addon::summary)
            af("Download count", addon::downloadCount)
            hf("Website URL", addon::websiteUrl)
            row {
                tableview(observableListOf(*addon.latestFiles)) {
                    fitToWidth(this@gridpane)
                    readonlyColumn("Name", AddonFile::fileName).pctWidth(30)
                    readonlyColumn("Date", AddonFile::fileDate).pctWidth(25)
                    readonlyColumn("File size", AddonFile::fileLength).pctWidth(15)
                    readonlyColumn("Versions", AddonFile::gameVersion) {
                        cellFormat { text = it.joinToString() }
                        remainingWidth()
                    }
                    smartResize()
                    gridpaneConstraints {
                        columnSpan = 2
                    }
                }
            }
        }
    }

    private fun GridPane.l(name: String, node: Node) = row {
        add(Label(name).apply { addClass("bolder") })
        add(node)
    }
    private inline fun GridPane.f(name: String, crossinline fn: () -> String) = l(name, Label(fn()))
    private inline fun GridPane.af(name: String, crossinline fn: () -> Any?) = l(name, Label(fn().toString()))
    private inline fun GridPane.hf(name: String, crossinline fn: () -> String) = l(name, Hyperlink(fn()).apply {
        setOnAction { hostServices.showDocument(this.text) }
    })

}