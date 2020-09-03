package szewek.mctool.app.task

import tornadofx.FXTask
import tornadofx.finally
import tornadofx.task
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

typealias TaskFunc = FXTask<*>.() -> Unit
typealias TaskQueue = Queue<TaskFunc>

fun createTaskQueue(vararg tt: FXTask<*>.() -> Unit) {
    val q = ArrayBlockingQueue<FXTask<*>.() -> Unit>(tt.size)
    q.addAll(tt)
    genTask(q)
}

private fun genTask(tt: TaskQueue) {
    val fn = tt.poll()
    if (fn != null) {
        TaskManager.addTask(task(func = fn).apply { finally { genTask(tt) } })
    }
}