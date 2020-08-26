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
        val background by cssproperty<Color>("-fx-background")
        val color by cssproperty<Color>("-fx-color")
        val controlInnerBackground by cssproperty<Color>("-fx-control-inner-background")
        val controlInnerBackgroundAlt by cssproperty<Color>("-fx-control-inner-background-alt")
        val lightTextColor by cssproperty<Color>("-fx-light-text-color")
        val midTextColor by cssproperty<Color>("-fx-mid-text-color")
        val darkTextColor by cssproperty<Color>("-fx-dark-text-color")
        val topColor by cssproperty<Color>("TOP-COLOR")
    }

    init {
        val base = Color.valueOf("rgb(72, 72, 72)")
        val col1 = Color.valueOf("rgb(40, 40, 40)")
        root {
            baseColor = base
            background set base
            //color set base.derive(-10.0)
            controlInnerBackground set col1
            controlInnerBackgroundAlt set col1.brighter()
            //darkTextColor set Color.valueOf("rgb(220, 220, 220)")
            //midTextColor set Color.valueOf("rgb(100, 100, 100)")
            //lightTextColor set col1
        }
        menuBar {
            backgroundColor = multi(base)
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