package szewek.mctool.app.recipe

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
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
    private val imgprop = Bindings.createObjectBinding({ Models.getImageOf(type.value) }, type, Models.compileState)

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

        children += ImageView().apply {
            imageProperty().bind(imgprop)
            isPreserveRatio = true
            fitWidth = 32.0 / ox
            fitHeight = 32.0 / oy
            setTopAnchor(this, 0.0)
            setLeftAnchor(this, 0.0)
        }
        val visible = count.greaterThan(1)
        children += Label().apply {
            textProperty().bind(count.asString())
            visibleWhen(visible)
            managedWhen(visible)
            setBottomAnchor(this, s)
            setRightAnchor(this, s)
        }

        setOnDragDetected(::dragDetected)
        setOnDragOver(::draggingOver)
        setOnDragDropped(::dragDropping)
        setOnDragDone(::dragDone)
    }

    private fun dragDetected(e: MouseEvent) {
        val tm = if (targetable && !e.isControlDown) TransferMode.MOVE else TransferMode.COPY
        val db = startDragAndDrop(tm)
        val cb = ClipboardContent()
        cb.putString(type.value)
        db.dragView = imgprop.value
        db.setContent(cb)
        e.consume()
    }

    private fun draggingOver(e: DragEvent) {
        if (e.gestureSource != this && targetable && e.dragboard.hasString()) {
            e.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
        }
        e.consume()
    }

    private fun dragDropping(e: DragEvent) {
        val db = e.dragboard
        var b = false
        if (db.hasString() && targetable) {
            type.value = db.string
            b = true
        }
        e.isDropCompleted = b
        e.consume()
    }

    private fun dragDone(e: DragEvent) {
        if (e.transferMode == TransferMode.MOVE && targetable) {
            type.value = ""
        }
    }
}