package szewek.mctool.app

import javafx.scene.Node
import javafx.scene.control.Tab
import tornadofx.UIComponent
import tornadofx.find

inline fun <reified T : UIComponent> Node.linkNewTab(noinline op: Tab.() -> Unit = {}) = this.setOnMouseClicked {
    find<MainView>().openTab<T>(op)
}