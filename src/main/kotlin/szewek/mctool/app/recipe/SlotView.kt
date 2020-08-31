package szewek.mctool.app.recipe

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import tornadofx.managedWhen
import tornadofx.visibleWhen

class SlotView(big: Boolean = false): AnchorPane() {
    val type = SimpleStringProperty("")
    private val count = SimpleIntegerProperty(0)

    init {
        val d = if (big) 48.0 else 32.0
        val s = if (big) 12.0 else 4.0
        setMinSize(d, d)
        setMaxSize(d, d)
        setPrefSize(d, d)
        style = "-fx-border-color: #888; -fx-border-width: 2px;"
        children += Label("?").apply {
            tooltip = Tooltip().apply { textProperty().bind(type) }
            alignment = Pos.CENTER
            setTopAnchor(this, s)
            setBottomAnchor(this, s)
            setLeftAnchor(this, s)
            setRightAnchor(this, s)
        }
        val visible = count.greaterThan(0)
        children += Label().apply {
            textProperty().bind(count.asString())
            visibleWhen(visible)
            managedWhen(visible)
            setBottomAnchor(this, s)
            setRightAnchor(this, s)
        }
    }
}