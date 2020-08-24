package szewek.mctool.app

import javafx.scene.Node
import javafx.scene.control.ButtonBase
import javafx.scene.control.Tab
import tornadofx.*
import kotlin.reflect.KClass

inline fun <reified T : UIComponent> Node.linkNewTab() = this.setOnMouseClicked {
    find<MainView>().openTab<T>()
}
inline fun <reified T : UIComponent> ButtonBase.linkNewTab() = this.setOnAction {
    find<MainView>().openTab<T>()
}

inline fun <reified T : UIComponent> Node.comesFrom() = properties[UI_COMPONENT_PROPERTY] is T
fun <T : UIComponent> Node.comesFrom(kc: KClass<T>) = kc.isInstance(properties[UI_COMPONENT_PROPERTY])