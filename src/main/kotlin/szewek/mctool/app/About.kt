package szewek.mctool.app

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import tornadofx.*

class About: Fragment("About") {
    override val root = BorderPane()

    init {
        root.top = HBox(
            Label("Craftery").apply { font = Font.font(20.0) }
        ).apply {
            padding = insets(4)
            alignment = Pos.CENTER
        }
        root.center = Label("© 2020-2021 Szewek")
        root.bottom = HBox(
            Button("Close").apply { setOnAction { this@About.close() } }
        ).apply {
            padding = insets(4)
            alignment = Pos.CENTER
        }
    }

    fun dialog() = openModal(resizable = false)?.apply {
        width = 200.0
        height = 150.0
    }
}