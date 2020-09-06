package szewek.mctool.app.lookup

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableView
import szewek.mctool.mcdata.ScanInfo

abstract class ModLookup<T>(fmt: String) {
    val list: ObservableList<T> = FXCollections.observableArrayList()
    val title: StringBinding = Bindings.size(list).asString(fmt)
    abstract val explain: String?
    abstract fun decorate(tv: TableView<T>)
    abstract fun gatherItems(si: ScanInfo): List<T>

    fun makeTable() = TableView<T>().apply {
        items = list
        decorate(this)
    }

    fun lazyGather(si: ScanInfo) {
        val l = gatherItems(si)
        Platform.runLater {
            list.setAll(l)
        }
    }
}