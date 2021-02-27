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
}