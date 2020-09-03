package szewek.mctool.app.task

import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ToggleButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import szewek.mctool.app.children
import tornadofx.*

class StatusBar: HBox() {

    init {
        addClass("status-bar")
        alignment = Pos.CENTER_LEFT
        children {
            val l = + Label().apply {
                paddingAll = 4
                maxWidth = Double.MAX_VALUE
                hgrow = Priority.ALWAYS
            }
            val pb = + ProgressBar().apply {
                val tnn = Bindings.isNotNull(TaskManager.lastTask)
                visibleWhen(tnn)
                managedWhen(tnn)
            }
            TaskManager.lastTask.addListener { _, _, v ->
                if (v != null) {
                    l.textProperty().cleanBind(v.messageProperty())
                    pb.progressProperty().cleanBind(v.progressProperty())
                } else {
                    l.textProperty().unbind()
                    l.text = ""
                    pb.progressProperty().unbind()
                }
            }
            + ToggleButton("Tasks").apply {
                isFocusTraversable = false
                TaskManager.taskListOpen.bind(selectedProperty())
            }
        }
    }
}