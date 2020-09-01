package szewek.mctool.app.task

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.WeakInvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.event.EventType
import tornadofx.minus
import tornadofx.observableListOf

object TaskManager {
    val taskListOpen = SimpleBooleanProperty(false)
    val tasks = observableListOf<Task<*>>()
    val lastTask = LatestTaskProperty()

    fun addTask(t: Task<*>) {
        tasks += t
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

    class LatestTaskProperty: ObjectBinding<Task<*>?>() {
        init {
            bind(tasks)
        }

        override fun computeValue() = tasks.findLast { !it.isDone }
        override fun getDependencies() =  observableListOf(tasks)
    }
}