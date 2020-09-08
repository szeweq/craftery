package szewek.mctool.app

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import szewek.mctool.app.lookup.*
import szewek.mctool.cfapi.CurseforgeAPI
import szewek.mctool.layout.LoaderPane
import szewek.mctool.mcdata.Modpack
import szewek.mctool.mcdata.ScanInfo
import szewek.mctool.util.Downloader
import szewek.mctool.util.FileLoader
import tornadofx.View
import tornadofx.bind
import tornadofx.cleanBind
import tornadofx.paddingAll
import java.util.zip.ZipInputStream

class Lookup(
        name: String,
        private val loader: FileLoader,
        private val modpack: Boolean = false
): View("Lookup: $name") {
    override val root = LoaderPane()
    private val lookups = FXCollections.observableArrayList(
            ListResourceData(),
            DetectCapabilities(),
            StaticFields(),
            SuspiciousLazyOptionals(),
            ListAllTags()
    )
    private val index = SimpleIntegerProperty()
    private val pages = FXCollections.observableArrayList<Node>()

    init {
        pages.bind(lookups) {
            val tv = it.makeTable()
            if (it.explain == null) tv else {
                val vb = VBox(Label(it.explain).apply { paddingAll = 4 }, tv)
                VBox.setVgrow(tv, Priority.ALWAYS)
                vb
            }
        }

        root.children += BorderPane().apply {
            centerProperty().bind(Bindings.valueAt(pages, index))
            left = ListView(lookups).apply {
                maxWidth = 200.0
                cellFormat {
                    textProperty().cleanBind(item.title)
                }
                selectionModel.apply {
                    select(index.get())
                    index.bind(selectedIndexProperty())
                }
                isFocusTraversable = false
            }
        }

        processLookups()
    }

    private fun processLookups() {
        root.launchTask {
            val si = ScanInfo()
            if (modpack) {
                updateMessage("Downloading modpack...")
                updateProgress(0, 1)
                val fi = loader.load(::updateProgress)
                updateMessage("Reading manifest...")
                updateProgress(0, 1)
                val files = Modpack.readManifest(ZipInputStream(fi))
                val l = files.size
                files.forEachIndexed { i, (pid, fid) ->
                    updateMessage("Getting file URL [$i / $l]...")
                    updateProgress(0, 1)
                    val murl = CurseforgeAPI.downloadURL(pid, fid)
                    if (!murl.endsWith(".jar")) { return@forEachIndexed }
                    val mname = murl.substringAfterLast('/')
                    updateMessage("Downloading $mname...")
                    val mf = Downloader.downloadFile(murl, ::updateProgress)
                    updateMessage("Scanning $mname...")
                    si.scanArchive(ZipInputStream(mf))
                }
            } else {
                updateMessage("Downloading file...")
                updateProgress(0, 1)
                val fi = loader.load(::updateProgress)
                updateMessage("Scanning classes...")
                updateProgress(0, 1)
                si.scanArchive(ZipInputStream(fi))
            }

            updateMessage("Gathering results...")
            updateProgress(2, 3)
            for (l in lookups) {
                l.lazyGather(si)
            }
            updateProgress(3, 3)
        }
    }
}