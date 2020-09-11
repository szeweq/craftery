package szewek.mctool.app

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import szewek.mctool.app.recipe.CraftingView
import szewek.mctool.app.recipe.SlotView
import szewek.mctool.app.task.createTaskQueue
import szewek.mctool.mcdata.MinecraftData
import szewek.mctool.mcdata.Models
import tornadofx.View
import tornadofx.bind

class RecipeCreator: View("Create recipes") {
    override val root = GridPane()
    private val recipeType = SimpleStringProperty("")
    private val allRecipes = FXCollections.observableArrayList<String>()

    init {
        val pct50 = root.widthProperty().divide(2)

        root.addRow(0, HBox().children {
            + ComboBox(allRecipes).apply { bind(recipeType) }
        })
        root.addRow(
                1,
                VBox(CraftingView()).apply { prefWidthProperty().bind(pct50) },
                ScrollPane(TilePane().apply {
                    alignment = Pos.TOP_LEFT
                }.children { for (i in 0..26) { + SlotView() } }).apply {
                    prefWidthProperty().bind(pct50)
                    isFitToWidth = true
                    isFitToHeight = true
                }
        )
        root.rowConstraints.addAll(
                RowConstraints(), RowConstraints().apply { vgrow = Priority.ALWAYS }
        )

        runTasks()
    }

    private fun runTasks() {
        createTaskQueue(
                MinecraftData.loadAllFilesFromJar(null),
                Models.compile()
        )
    }
}