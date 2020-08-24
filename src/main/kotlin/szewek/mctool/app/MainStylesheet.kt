package szewek.mctool.app

import tornadofx.*

class MainStylesheet: Stylesheet() {
    init {
        s(textField, button, comboBox, tab) {
            backgroundRadius = multi(box(0.px))
        }
    }
}