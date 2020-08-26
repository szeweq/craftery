package szewek.mctool.app

import javafx.scene.Node
import javafx.scene.control.ButtonBase
import javafx.scene.layout.Region
import tornadofx.UIComponent
import tornadofx.UI_COMPONENT_PROPERTY
import tornadofx.find
import kotlin.reflect.KClass

inline fun <reified T : UIComponent> Node.linkNewTab() = this.setOnMouseClicked {
    find<MainView>().openTab<T>()
}
inline fun <reified T : UIComponent> ButtonBase.linkNewTab() = this.setOnAction {
    find<MainView>().openTab<T>()
}

inline fun <reified T : UIComponent> Node.comesFrom() = properties[UI_COMPONENT_PROPERTY] is T
fun <T : UIComponent> Node.comesFrom(kc: KClass<T>) = kc.isInstance(properties[UI_COMPONENT_PROPERTY])

fun Region.widthFrom(other: Region) = apply { prefWidthProperty().bind(other.widthProperty()) }
fun Region.heightFrom(other: Region) = apply { prefHeightProperty().bind(other.heightProperty()) }