package szewek.mctool.layout

import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import szewek.mctool.app.css
import szewek.mctool.app.task.TaskManager
import tornadofx.*

class LoaderPane: StackPane() {
    private val progress = ProgressBar()
    private val msg = Label()
    private val box = VBox()

    init {
        box.addClass("loading-box")
        box += msg
        msg.graphic = progress
        progress.prefWidthProperty().bind(box.widthProperty() / 2)
    }

    override fun getUserAgentStylesheet(): String? = css()

    fun <T> launchTask(func: FXTask<*>.() -> T) {
        children.add(box)
        val t = task {
            try {
                func()
            } finally {
                Platform.runLater(::onTaskFinish)
            }
        }
        TaskManager.addTask(t)
        progress.bind(t.progressProperty(), true)
        msg.bind(t.messageProperty(), true)
    }

    private fun onTaskFinish() {
        progress.progressProperty().unbind()
        msg.textProperty().unbind()
        children.remove(box)
    }
}