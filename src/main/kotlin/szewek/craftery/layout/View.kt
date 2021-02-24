package szewek.craftery.layout

import androidx.compose.runtime.Composable

abstract class View(val title: String) {
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