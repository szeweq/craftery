package szewek.mctool.app

import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.multi
import tornadofx.px

class MainStylesheet: Stylesheet() {
    companion object {
        val mbZero = multi(box(0.px))
    }

    init {
        s(textField, button, comboBox, tab, toggleButton) {
            backgroundRadius = mbZero
        }
        progressBar { bar { backgroundRadius = mbZero } }
    }
}