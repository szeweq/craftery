package szewek.mctool.app.recipe

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import szewek.mctool.mcdata.Models
import tornadofx.managedWhen
import tornadofx.visibleWhen

class SlotView(name: String = "minecraft:item/golden_shovel", big: Boolean = false): AnchorPane() {
    val type = SimpleStringProperty(name)
    private val count = SimpleIntegerProperty(0)

    init {
        val d = if (big) 48.0 else 36.0
        val s = if (big) 3.0 else 2.0
        setMinSize(d, d)
        setMaxSize(d, d)
        setPrefSize(d, d)
        style = "-fx-border-color: #888; -fx-border-width: 2px;"
        children += ImageView().apply {
            imageProperty().bind(Bindings.createObjectBinding({ Models.getImageOf(type.value) }, type, Models.compileState))
            //tooltip = Tooltip().apply { textProperty().bind(type) }
            //alignment = Pos.CENTER
            isSmooth = false
            fitWidth = d - s * 2
            fitHeight = d - s * 2
            setTopAnchor(this, 0.0)
            setBottomAnchor(this, 0.0)
            setLeftAnchor(this, 0.0)
            setRightAnchor(this, 0.0)
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