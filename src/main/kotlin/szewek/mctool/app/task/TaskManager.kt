package szewek.mctool.app.task

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.concurrent.Worker
import szewek.mctool.app.objectBinding
import tornadofx.FXTask
import tornadofx.finally
import tornadofx.observableListOf
import tornadofx.task
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

object TaskManager {
    val taskListOpen = SimpleBooleanProperty(false)
    val tasks = observableListOf<Task<*>>()
    val lastTask = tasks.objectBinding { findLast { !it.isDone } }

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

    private fun removeOnFinish(t: Task<*>) {
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
}

typealias TaskFunc = FXTask<*>.() -> Unit
typealias TaskQueue = Queue<TaskFunc>