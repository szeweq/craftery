package szewek.mctool.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import tornadofx.*

class MainView: View() {
    private val tabPane = TabPane()
    private val hasTabs: BooleanBinding = Bindings.isNotEmpty(tabPane.tabs)
    private val welcome = Welcome()
    override val root = BorderPane()

    init {
        primaryStage.apply {
            title = "MCTool"
            width = 800.0
            height = 480.0
        }
        tabPane.apply {
            visibleProperty().cleanBind(hasTabs)
            tab<ModSearch>()
        }
        root.apply {
            title = "MCTool"
            center = tabsWithEmptyPage(welcome.root)
        }
    }

    fun openTab(ui: UIComponent, op: Tab.() -> Unit = {}) = tabPane.tab(ui, op)

    inline fun <reified T : UIComponent> openTab(noinline op: Tab.() -> Unit = {}) = openTab(find<T>(), op)

    private inline fun <T: Node> tabsWithEmptyPage(node: T, crossinline op: T.() -> Unit = {}) = stackpane {
        tabPane.attachTo(this)
        node.visibleProperty().cleanBind(hasTabs.not())
        node.attachTo(this, op)
    }
}