package szewek.mctool.app

import javafx.scene.control.MenuBar
import javafx.stage.FileChooser
import szewek.mctool.util.ZipLoader
import tornadofx.chooseFile
import tornadofx.item
import tornadofx.menu

class AppMenu(mainView: MainView) : MenuBar() {
    init {
        menu("App") {
            item("Search mods").setOnAction {
                mainView.selectOrOpenTab<ModSearch>()
            }
            item("Scan local file...").setOnAction {
                val files = chooseFile(
                        "Choose JAR file",
                        arrayOf(FileChooser.ExtensionFilter("JAR File", "*.jar")),
                        owner = mainView.currentWindow
                )
                if (files.isNotEmpty()) {
                    val f = files[0]
                    mainView.openTab(LookupMod(f.name, ZipLoader.FromFile(f)))
                }
            }
            item("About").setOnAction {
                About().dialog()
            }
            item("Quit").setOnAction {
                mainView.close()
            }
        }
    }
}