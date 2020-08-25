package szewek.mctool.app

import javafx.scene.text.FontWeight
import tornadofx.*

class MainStylesheet: Stylesheet() {
    companion object {
        val mbZero = multi(box(0.px))
    }

    init {
        s(textField, button, comboBox, tab) {
            backgroundRadius = mbZero
        }
        s(".tab-pane:focused > .tab-header-area > .headers-region > .tab:selected .focus-indicator") {
            borderWidth = mbZero
            borderRadius = mbZero
        }
        s(".bolder") {
            fontWeight = FontWeight.BOLD
        }
        s(".page-header") {
            padding = box(4.px)

            label {
                fontSize = 16.px
                fontWeight = FontWeight.BOLD
            }
        }
        s(".page-info") {
            padding = box(8.px)
        }
    }
}