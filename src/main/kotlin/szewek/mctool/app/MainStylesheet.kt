package szewek.mctool.app

import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import tornadofx.*

class MainStylesheet: Stylesheet() {
    companion object {
        val mbZero = multi(box(0.px))
        val loadingBox by cssclass()
    }

    init {
        val base = Color.valueOf("#ececec")
        root {
            baseColor = base
        }
        s(textField, button, comboBox, tab) {
            backgroundRadius = mbZero
        }
        s(".tab-pane:focused > .tab-header-area > .headers-region > .tab:selected .focus-indicator") {
            borderWidth = mbZero
            borderRadius = mbZero
        }
        progressBar { bar { backgroundRadius = mbZero } }
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
        loadingBox {
            backgroundColor = multi(c(0, 0, 0, 0.5))
            alignment = Pos.CENTER

            label {
                backgroundColor = multi(base)
                minWidth = 100.px
                fontSize = 16.px
                contentDisplay = ContentDisplay.BOTTOM
                graphicTextGap = 8.px
                padding = box(8.px)
            }
        }
    }
}