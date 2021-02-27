package szewek.craftery.layout

import androidx.compose.runtime.*

abstract class View(val title: String) {
    val progress = ProgressState()
    lateinit var selection: SingleSelection

    val isActive: Boolean
        get() = selection.selected == this

    fun activate() {
        selection.selected = this
    }

    var close: (() -> Unit)? = null

    @Composable
    abstract fun content()

    class ProgressState {
        var value by mutableStateOf(Float.MAX_VALUE)

        fun setIndeterminate() {
            value = -1f
        }

        fun setFinished() {
            value = Float.MAX_VALUE
        }

        fun setFraction(a: Long, b: Long) {
            value = a.toFloat() / b
        }
    }
}