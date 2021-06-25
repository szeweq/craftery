package szewek.craftery.layout

import androidx.compose.runtime.mutableStateListOf
import kotlin.reflect.KClass

/**
 * Manages active views displayed in an app.
 */
object ViewManager {
    private val selection = SingleSelection()

    val active: View? get() = selection.selected as View?

    var views = mutableStateListOf<View>()
        private set

    fun open(v: View) {
        v.selection = selection
        v.close = { close(v) }
        views.add(v)
        v.activate()
    }

    fun <T: View> selectOrOpen(kc: KClass<T>) {
        val v = views.find { kc.isInstance(it) }
        if (v != null) {
            v.activate()
        } else {
            open(kc.java.getDeclaredConstructor().newInstance())
        }
    }

    fun <T: View> selectOrOpenInstance(obj: T) {
        val b = views.contains(obj);
        if (b) {
            obj.activate()
        } else {
            open(obj)
        }
    }

    fun <T: View> selectOrOpenAction(kc: KClass<T>): () -> Unit = { selectOrOpen(kc) }
    inline fun <reified T: View> selectOrOpenAction() = selectOrOpenAction(T::class)

    fun <T: View> selectOrOpenInstanceAction(obj: T): () -> Unit = { selectOrOpenInstance(obj) }

    private fun close(v: View) {
        v.onClose()
        val index = views.indexOf(v)
        views.remove(v)
        if (v.isActive) {
            selection.selected = views.getOrNull(index.coerceAtMost(views.lastIndex))
        }
    }
}