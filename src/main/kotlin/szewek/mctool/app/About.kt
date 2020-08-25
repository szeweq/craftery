package szewek.mctool.app

import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.text.Font
import tornadofx.*

class About: Fragment("About") {
    override val root = BorderPane()

    init {
        root.top = hbox(alignment = Pos.CENTER) {
            padding = insets(4)
            text("MCTool") {
                font = Font.font(20.0)
            }
        }
        root.center = text("About MCTool")
        root.bottom = hbox(alignment = Pos.CENTER) {
            padding = insets(4)
            button("Close").setOnAction { this@About.close() }
        }
    }

    fun dialog() = openModal(resizable = false)?.apply {
        width = 200.0
        height = 150.0
    }
}