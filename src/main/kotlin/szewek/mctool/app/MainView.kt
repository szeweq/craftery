package szewek.mctool.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.scene.Node
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import tornadofx.*
import kotlin.reflect.KClass

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
            tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS
            visibleProperty().cleanBind(hasTabs)
        }
        root.apply {
            title = "MCTool"
            top = menubar {
                menu("App") {
                    item("Lookup mods").setOnAction {
                        selectOrOpenTab<ModSearch>()
                    }
                    item("About").setOnAction {
                        About().dialog()
                    }
                    item("Quit").setOnAction {
                        this@MainView.close()
                    }
                }
            }
            center = tabsWithEmptyPage(welcome.root)
        }
        openTab<ModSearch>()
    }

    fun openTab(ui: UIComponent) = with(tabPane) {
        val tab = tab(ui)
        tab.textProperty().cleanBind(ui.titleProperty)
        selectionModel.select(tabs.lastIndex)
        tab
    }
    inline fun <reified T : UIComponent> openTab() = openTab(find<T>())

    fun <T: UIComponent> selectOrOpenTab(kc: KClass<T>) = with(tabPane) {
        tabs.find { it.content.comesFrom(kc) }.apply { selectionModel.select(this) }
    } ?: openTab(find(kc))
    inline fun <reified T : UIComponent> selectOrOpenTab() = selectOrOpenTab(T::class)

    private inline fun <T: Node> tabsWithEmptyPage(node: T, crossinline op: T.() -> Unit = {}) = stackpane {
        tabPane.attachTo(this)
        node.visibleProperty().cleanBind(hasTabs.not())
        node.attachTo(this, op)
    }
}