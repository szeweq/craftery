package szewek.mctool.app

import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import tornadofx.Fragment
import tornadofx.button
import tornadofx.text

class Welcome: Fragment("Welcome") {
    override val root = VBox()

    init {
        root.apply {
            alignment = Pos.CENTER
            text("What would you like to do?") {
                font = Font.font(28.0)
            }
            button("Lookup fields in a mod") {
                linkNewTab<ModSearch>()
            }
        }
    }
}