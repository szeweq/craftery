package szewek.mctool.app

import javafx.application.Application
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.ButtonBase
import javafx.scene.layout.Region
import tornadofx.*
import java.util.concurrent.Callable
import kotlin.reflect.KClass

inline fun <reified T : UIComponent> Node.comesFrom() = properties[UI_COMPONENT_PROPERTY] is T
fun <T : UIComponent> Node.comesFrom(kc: KClass<T>) = kc.isInstance(properties[UI_COMPONENT_PROPERTY])

inline fun <reified T : Application> import(cssFile: String) = importStylesheet(T::class.java.getResource(cssFile).toExternalForm())
inline fun <reified T : Node> T.css(): String? = T::class.java.let { it.getResource("/css/${it.simpleName}.css").toExternalForm() }

inline fun <T, O> ObservableList<T>.objectBinding(
        vararg deps: Observable,
        crossinline fn: ObservableList<T>.() -> O
): ObjectBinding<O> = Bindings.createObjectBinding({ fn(this) }, this, *deps)