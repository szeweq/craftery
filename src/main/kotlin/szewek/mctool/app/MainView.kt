package szewek.mctool.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import szewek.mctool.app.task.StatusBar
import szewek.mctool.app.task.TaskListView
import tornadofx.*
import kotlin.reflect.KClass

class MainView: View() {
    private val tasksOpen = SimpleBooleanProperty(false)
    private val tabPane = TabPane()
    private val hasTabs: BooleanBinding = Bindings.isNotEmpty(tabPane.tabs)
    private val welcome = Welcome()
    private val tasks = TaskListView()
    private val statusBar = StatusBar()
    override val root = BorderPane()

    init {
        primaryStage.apply {
            title = "MCTool"
            width = 800.0
            height = 600.0
        }
        tabPane.apply {
            tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS
            visibleWhen(hasTabs)
            managedWhen(hasTabs)
        }
        root.apply {
            title = "MCTool"
            top = AppMenu(this@MainView)
            centerProperty().bind(Bindings.`when`(hasTabs).then<Node>(tabPane).otherwise(welcome.root))
            right = tasks
            bottom = statusBar
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
}