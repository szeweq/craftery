package szewek.craftery.layout

import androidx.compose.runtime.*

abstract class View(title: String) {
    val title = mutableStateOf(title)
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