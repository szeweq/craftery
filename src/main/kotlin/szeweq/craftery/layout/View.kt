package szeweq.craftery.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import szeweq.craftery.util.Selection
import szeweq.desktopose.progress.ProgressState

abstract class View(title: String) {
    val title = mutableStateOf(title)
    val progress = ProgressState()
    val viewScope = CoroutineScope(Dispatchers.Default)
    lateinit var selection: Selection<*>

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    var close: (() -> Unit)? = null

    fun tryClose() {
        close?.invoke()
    }

    fun onClose() {
        runCatching { viewScope.cancel() }
    }

    @Composable
    abstract fun content()
}