package szewek.mctool.app

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import tornadofx.*

class Welcome: Fragment("Welcome") {
    override val root = VBox()

    init {
        root.apply {
            alignment = Pos.CENTER
            label("What would you like to do?") {
                padding = insets(8.0)
                font = Font.font(28.0)
            }
            hbox {
                maxWidthProperty().bind(root.widthProperty() * 0.4)
                actions.forEach {
                    button(it.text).apply {
                        widthFrom(this@hbox)
                        onAction = it.action
                    }
                }
            }
        }
    }

    class Action(val text: String, val action: EventHandler<ActionEvent>)

    companion object {
        val actions = observableListOf(
            "Lookup fields in a mod" { openTab<ModSearch>() }
        )

        private operator fun String.invoke(action: EventHandler<ActionEvent>) = Action(this, action)
        private inline fun <reified T : UIComponent> openTab() = find<MainView>().openTab<T>()
    }
}