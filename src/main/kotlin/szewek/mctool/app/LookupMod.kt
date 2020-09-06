package szewek.mctool.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import szewek.mctool.app.lookup.DetectCapabilities
import szewek.mctool.app.lookup.ListResourceData
import szewek.mctool.app.lookup.StaticFields
import szewek.mctool.app.lookup.SuspiciousLazyOptionals
import szewek.mctool.layout.LoaderPane
import szewek.mctool.mcdata.Scanner
import szewek.mctool.util.FileLoader
import tornadofx.View
import tornadofx.bind
import tornadofx.cleanBind
import tornadofx.paddingAll
import java.util.zip.ZipInputStream

class LookupMod(name: String, private val loader: FileLoader): View("Lookup: $name") {
    override val root = LoaderPane()
    private val lookups = FXCollections.observableArrayList(
            ListResourceData(),
            DetectCapabilities(),
            StaticFields(),
            SuspiciousLazyOptionals()
    )
    private val index = SimpleIntegerProperty()
    private val pages = FXCollections.observableArrayList<Node>()
    private val list = FXCollections.observableArrayList<StringBinding>()

    init {
        pages.bind(lookups) {
            val tv = it.makeTable()
            if (it.explain == null) tv else {
                val vb = VBox()
                vb.children += Label(it.explain).apply { paddingAll = 4 }
                vb.children += tv
                VBox.setVgrow(tv, Priority.ALWAYS)
                vb
            }
        }
        list.bind(lookups) {
            it.title
        }

        root.children += BorderPane().apply {
            centerProperty().bind(Bindings.valueAt(pages, index))
            left = ListView<StringBinding>().apply {
                maxWidth = 200.0
                items = list
                cellFormat {
                    textProperty().cleanBind(item)
                }
                selectionModel.apply {
                    select(index.get())
                    index.bind(selectedIndexProperty())
                }
                isFocusTraversable = false
            }
        }

        lookupFields()
    }

    private fun lookupFields() {
        root.launchTask {
            updateMessage("Downloading file...")
            updateProgress(0, 1)
            val fi = loader.load(::updateProgress)
            updateMessage("Scanning classes...")
            updateProgress(0, 1)
            val si = Scanner.scanArchive(ZipInputStream(fi))
            updateMessage("Gathering results...")
            updateProgress(2, 3)
            for (l in lookups) {
                l.lazyGather(si)
            }
            updateProgress(3, 3)
        }
    }
}