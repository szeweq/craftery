package szewek.craftery.layout

import androidx.compose.runtime.mutableStateListOf
import kotlin.reflect.KClass

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

    inline fun <reified T: View> selectOrOpen() = selectOrOpen(T::class)

    private fun close(v: View) {
        val index = views.indexOf(v)
        views.remove(v)
        if (v.isActive) {
            selection.selected = views.getOrNull(index.coerceAtMost(views.lastIndex))
        }
    }
}