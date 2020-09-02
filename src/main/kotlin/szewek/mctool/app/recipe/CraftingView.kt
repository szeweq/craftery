package szewek.mctool.app.recipe

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.TilePane
import szewek.mctool.app.children

class CraftingView: HBox(4.0) {
    init {
        alignment = Pos.CENTER
        children += TilePane().apply {
            setPrefSize(36.0 * 3, 36.0 * 3)
            minWidthProperty().bind(prefWidthProperty())
            minHeightProperty().bind(prefHeightProperty())
            prefColumns = 3
            prefRows = 3
        } children {
            for (i in 0..8) {
                + SlotView("minecraft:item/dirt")
            }
        }
        children += SlotView("minecraft:item/flint", true)
    }
}