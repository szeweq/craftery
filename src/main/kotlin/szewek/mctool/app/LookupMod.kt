package szewek.mctool.app

import javafx.application.Platform
import javafx.geometry.Side
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import szewek.mctool.app.lookup.*
import szewek.mctool.layout.LoaderPane
import szewek.mctool.mcdata.Scanner
import szewek.mctool.util.FileLoader
import tornadofx.View
import tornadofx.paddingAll
import tornadofx.runLater
import java.util.zip.ZipInputStream

class LookupMod(name: String, private val loader: FileLoader): View("Lookup: $name") {
    private val lookups = setOf(
            ListResourceData(),
            DetectCapabilities(),
            StaticFields(),
            SuspiciousLazyOptionals()
    )
    override val root = LoaderPane()
    private val nav = TabPane()

    init {
        nav.side = Side.LEFT
        nav.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        root.children += nav

        lookups.forEach { lookupTab(it) }

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

    private fun <T> lookupTab(ml: ModLookup<T>) {
        val tv = ml.makeTable()
        val tab = Tab()
        tab.textProperty().bind(ml.title)
        tab.content = if (ml.explain == null) tv else {
            val vb = VBox()
            vb.children += Label(ml.explain).apply { paddingAll = 4 }
            vb.children += tv
            VBox.setVgrow(tv, Priority.ALWAYS)
            vb
        }
        nav.tabs += tab
    }
}