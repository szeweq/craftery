package szewek.mctool.app

import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.paint.Color
import javafx.scene.paint.Stop
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
        progressBar { bar { backgroundRadius = mbZero } }
    }
}