package szewek.mctool.app

import tornadofx.*

class MainStylesheet: Stylesheet() {
    init {
        s(textField, button, tab) {
            backgroundRadius = multi(box(0.px))
        }
    }
}