package szewek.mctool.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import tornadofx.*


class MainView: View() {
    val tabPane = TabPane()
    private val hasTabs: BooleanBinding = Bindings.isNotEmpty(tabPane.tabs)
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
            center = tabsWithEmptyPage(HBox()) {
                alignment = Pos.CENTER
                text("What would you like to do?") {
                    font = Font.font(28.0)
                }
            }
        }
    }

    private inline fun <T: Node> tabsWithEmptyPage(node: T, crossinline op: T.() -> Unit = {}) = stackpane {
        tabPane.attachTo(this)
        node.visibleProperty().cleanBind(hasTabs.not())
        node.attachTo(this, op)
    }
}