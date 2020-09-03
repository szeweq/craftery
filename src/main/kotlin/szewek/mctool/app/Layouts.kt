package szewek.mctool.app

import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import tornadofx.addChildIfPossible

inline infix fun <T: Parent> T.children(fn: ChildrenOf<T>.() -> Unit): T {
    fn(ChildrenOf(this))
    return this
}

inline class ChildrenOf<T: Parent>(val _parent: Parent) {
    inline operator fun <reified N: Node> N.unaryPlus(): N = apply { _parent.addChildIfPossible(this) }
}

inline fun MenuBar.build(fn: MenuBarBuilder.() -> Unit): MenuBar {
    fn(MenuBarBuilder(this))
    return this
}

inline class MenuBarBuilder(val bar: MenuBar) {
    inline infix fun String.menu(fn: MenuBuilder.() -> Unit) = Menu(this).apply {
        fn(MenuBuilder(this))
        bar.menus += this
    }
}
inline class MenuBuilder(val menu: Menu) {
    inline infix fun String.item(fn: MenuItem.() -> Unit) = MenuItem(this).apply {
        fn()
        menu.items += this
    }
    inline infix fun String.action(crossinline fn: MenuItem.(ActionEvent) -> Unit) = this item {
        setOnAction { fn(it) }
    }
}