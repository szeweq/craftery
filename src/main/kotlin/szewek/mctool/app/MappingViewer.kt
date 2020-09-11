package szewek.mctool.app

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ProgressCallback
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import szewek.mctool.util.Downloader
import tornadofx.*

class MappingViewer: View("Mapping viewer") {
    override val root = BorderPane()
    private val mavenURL = SimpleStringProperty("")
    private val filterName = SimpleStringProperty("")
    private val mcpVersions = FXCollections.observableArrayList<String>()
    private val mappingVersions = FXCollections.observableArrayList<String>()
    private val mappingList = FXCollections.observableArrayList<Mapping>()
    private val selectedMcp = SimpleStringProperty()
    private val selectedMapping = SimpleStringProperty()

    init {
        root.left = VBox().with {
            + TextField().apply { bind(mavenURL) }
            + Button("Update").apply {
                val mb = mavenURL.isEmpty
                disableWhen(mb)
                setOnAction { if (!mb.get()) listVersions() }
            }
            + ComboBox(mcpVersions).apply { valueProperty().bind(selectedMcp) }
            + ComboBox(mappingVersions).apply { valueProperty().bind(selectedMapping) }
            + Button("Create list").apply {
                val b = selectedMcp.isNull or selectedMapping.isNull
                disableWhen(b)
                setOnAction { if (!b.get()) listMappings() }
            }
        }
        root.right = VBox().with {
            + HBox().with {
                + TextField().apply { bind(filterName) }
            }
            + TableView(mappingList).apply {
                readonlyColumn("Name", Mapping::name)
                readonlyColumn("Class", Mapping::cl)
                readonlyColumn("Mapped", Mapping::mapped)
            }
        }
    }

    private fun listVersions() {
        task {
            val murl = mavenURL.get()
            mavenGet(murl, "mcp_config/maven-metadata.xml") // Response XML
            mavenGet(murl, "mcp_snapshot/maven-metadata.xml")
        }
    }

    private fun listMappings() {
        task {
            val murl = mavenURL.get()
            mavenPkg(murl, "mcp_config", selectedMcp.get(), "zip", ::updateProgress) // Unzip
            mavenPkg(murl, "mcp_snapshot", selectedMapping.get(), "zip", ::updateProgress)
        }
    }

    class Mapping(val name: String, val cl: String, val mapped: String)

    companion object {
        fun mavenGet(murl: String, path: String) = Fuel.get("$murl/de/oceanlabs/mcp/$path")
        fun mavenPkg(murl: String, name: String, version: String, ext: String, progress: ProgressCallback)
                = Downloader.downloadFile("$murl/de/oceanlabs/mcp/$name/$version/$name-$version.$ext", progress)
    }
}