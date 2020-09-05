package szewek.mctool.app.recipe

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.AnchorPane
import javafx.stage.Screen
import szewek.mctool.mcdata.Models
import tornadofx.managedWhen
import tornadofx.visibleWhen

class SlotView(
        name: String = "minecraft:item/golden_shovel",
        val targetable: Boolean = false
): AnchorPane() {
    val type = SimpleStringProperty(name)
    private val count = SimpleIntegerProperty(0)

    init {
        val ps = Screen.getPrimary()
        val ox = ps.outputScaleX
        val oy = ps.outputScaleY

        val d = 34.0
        val s = 2.0
        setMinSize(d / ox, d / oy)
        setMaxSize(d / ox, d / oy)
        setPrefSize(d / ox, d / oy)
        style = "-fx-border-color: #AAA #666 #666 #AAA; -fx-border-width: 1px;"

        val imgprop = Bindings.createObjectBinding({ Models.getImageOf(type.value) }, type, Models.compileState)

        children += ImageView().apply {
            imageProperty().bind(imgprop)
            isPreserveRatio = true
            fitWidth = 32.0 / ox
            fitHeight = 32.0 / oy
            setTopAnchor(this, 0.0)
            //setBottomAnchor(this, 0.0)
            setLeftAnchor(this, 0.0)
            //setRightAnchor(this, 0.0)
        }
        val visible = count.greaterThan(1)
        children += Label().apply {
            textProperty().bind(count.asString())
            visibleWhen(visible)
            managedWhen(visible)
            setBottomAnchor(this, s)
            setRightAnchor(this, s)
        }

        setOnDragDetected {
            val tm = if (targetable && !it.isControlDown) TransferMode.MOVE else TransferMode.COPY
            val db = startDragAndDrop(tm)
            val cb = ClipboardContent()
            cb.putString(type.value)
            db.dragView = imgprop.value
            db.setContent(cb)
            it.consume()
        }
        setOnDragOver {
            if (it.gestureSource != this && targetable && it.dragboard.hasString()) {
                it.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
            }
            it.consume()
        }
        setOnDragDropped {
            val db = it.dragboard
            var b = false
            if (db.hasString() && targetable) {
                type.value = db.string
                b = true
            }
            it.isDropCompleted = b
            it.consume()
        }
        setOnDragDone {
            if (it.transferMode == TransferMode.MOVE && targetable) {
                type.value = ""
            }
        }
    }
}