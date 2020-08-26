package szewek.mctool.app

import tornadofx.App
import tornadofx.reloadStylesheetsOnFocus

class MCToolApp: App(MainView::class, MainStylesheet::class) {
    init {
        reloadStylesheetsOnFocus()
        import<MCToolApp>("/main.css")
    }
}