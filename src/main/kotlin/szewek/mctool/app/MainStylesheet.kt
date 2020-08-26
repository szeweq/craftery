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
        val loadingBox by cssclass()
        val background by cssproperty<Color>("-fx-background")
        val color by cssproperty<Color>("-fx-color")
        val controlInnerBackground by cssproperty<Color>("-fx-control-inner-background")
        val controlInnerBackgroundAlt by cssproperty<Color>("-fx-control-inner-background-alt")
        val lightTextColor by cssproperty<Color>("-fx-light-text-color")
        val midTextColor by cssproperty<Color>("-fx-mid-text-color")
        val darkTextColor by cssproperty<Color>("-fx-dark-text-color")
        val markColor by cssproperty<Color>("-fx-mark-color")
        val markHighlightColor by cssproperty<Color>("-fx-mark-highlight-color")
    }

    init {
        val base = Color.valueOf("rgb(72, 72, 72)")
        val col1 = Color.valueOf("rgb(40, 40, 40)")
        val acc = Color.valueOf("rgb(68, 120, 216)")
        root {
            baseColor = base
            accentColor = acc
            focusColor = acc.derive(-0.15)
            faintFocusColor = acc.deriveColor(0.0, 1.0, 0.85, 0.13)
            background set base
            color set base.derive(-0.1)
            controlInnerBackground set col1
            controlInnerBackgroundAlt set col1.brighter()
            //darkTextColor set Color.valueOf("rgb(220, 220, 220)")
            //midTextColor set Color.valueOf("rgb(100, 100, 100)")
            //lightTextColor set col1
            markColor set base.ladder(Stop(0.3, Color.WHITE), Stop(0.31, base.derive(-0.36)))
        }
        menuBar {
            backgroundColor = multi(base.derive(0.1))
        }
        s(textField, button, comboBox, tab) {
            backgroundRadius = mbZero
        }
        s(".tab-pane:top > .tab-header-area") { padding = box(0.px) }
        s(".tab-pane:bottom > .tab-header-area") { padding = box(0.px) }
        s(".tab-pane:left > .tab-header-area") { padding = box(0.px) }
        s(".tab-pane:right > .tab-header-area") { padding = box(0.px) }
        s(".tab-pane > .tab-header-area > .tab-header-background") {
            backgroundColor = multi(base.derive(0.1))
        }
        s(".tab-pane > .tab-header-area > .headers-region > .tab") {
            backgroundInsets = multi(box(0.px, 0.px, 0.px, 0.px), box(2.px, 0.px, 0.px, 0.px))
            backgroundColor = multi(base.derive(-0.15))
        }
        s(".tab-pane > .tab-header-area > .headers-region > .tab:selected") {
            backgroundColor = multi(acc, base)
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