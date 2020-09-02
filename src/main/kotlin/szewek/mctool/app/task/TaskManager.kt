package szewek.mctool.app.task

import javafx.application.Platform
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.concurrent.Worker
import tornadofx.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

object TaskManager {
    val taskListOpen = SimpleBooleanProperty(false)
    val tasks = observableListOf<Task<*>>()
    val lastTask = LatestTaskBinding()

    fun addTask(t: Task<*>) {
        if (Platform.isFxApplicationThread()) {
            tasks += t
            removeOnFinish(t)
        } else {
            Platform.runLater {
                tasks += t
                removeOnFinish(t)
            }
        }
    }

    fun removeOnFinish(t: Task<*>) {
        val csl = object : ChangeListener<Worker.State> {
            override fun changed(ov: ObservableValue<out Worker.State>, os: Worker.State?, ns: Worker.State?) {
                if (ns == Worker.State.SUCCEEDED || ns == Worker.State.CANCELLED || ns == Worker.State.FAILED) {
                    tasks.remove(t)
                    ov.removeListener(this)
                }
            }
        }
        t.stateProperty().addListener(csl)
    }

    fun createTaskQueue(vararg tt: FXTask<*>.() -> Unit) {
        val q = ArrayBlockingQueue<FXTask<*>.() -> Unit>(tt.size)
        q.addAll(tt)
        genTask(q)
    }

    private fun genTask(tt: TaskQueue) {
        val fn = tt.poll()
        if (fn != null) {
            addTask(task(func = fn).apply { finally { genTask(tt) } })
        }
    }

    class LatestTaskBinding: ObjectBinding<Task<*>?>() {
        init {
            bind(tasks)
        }

        override fun computeValue() = tasks.findLast { !it.isDone }
        override fun getDependencies() =  observableListOf(tasks)
    }
}

typealias TaskFunc = FXTask<*>.() -> Unit
typealias TaskQueue = Queue<TaskFunc>