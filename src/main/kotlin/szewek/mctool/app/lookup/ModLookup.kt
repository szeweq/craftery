package szewek.mctool.app.lookup

import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableView
import szewek.mctool.mcdata.ScanInfo

abstract class ModLookup<T>(private val fmt: String) {
    val list: ObservableList<T> = FXCollections.observableArrayList()
    val title = Bindings.size(list).asString(fmt)
    abstract val explain: String?
    abstract fun TableView<T>.decorate()
    abstract fun gatherItems(si: ScanInfo): List<T>

    fun makeTable() = TableView<T>().apply {
        items = list
        decorate()
    }

    fun lazyGather(si: ScanInfo): () -> Unit {
        val l = gatherItems(si)
        return {
            list.setAll(l)
        }
    }
}