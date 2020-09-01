package szewek.mctool.app.task

import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.VBox
import tornadofx.*

class TaskListView: VBox(2.0) {
    init {
        addClass("task-list")
        minWidth = 200.0
        maxWidth = 200.0
        visibleWhen(TaskManager.taskListOpen)
        managedWhen(TaskManager.taskListOpen)
        bindChildren(TaskManager.tasks) {
            val l = Label().apply {
                minHeight = 10.0
                textProperty().bind(it.messageProperty())
            }
            val pb = ProgressBar().apply {
                fitToWidth(this@TaskListView)
                progressProperty().bind(it.progressProperty())
            }
            VBox(l, pb).addClass("task")
        }
    }
}