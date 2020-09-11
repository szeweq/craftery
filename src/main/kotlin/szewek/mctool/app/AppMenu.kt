package szewek.mctool.app

import javafx.scene.control.MenuBar
import javafx.stage.FileChooser
import szewek.mctool.util.FileLoader
import tornadofx.chooseFile

class AppMenu(mainView: MainView) : MenuBar() {
    init {
        build {
            "App" menu {
                "Scan local file..." action {
                    val files = chooseFile(
                        "Choose JAR file",
                        arrayOf(FileChooser.ExtensionFilter("JAR File", "*.jar")),
                        owner = mainView.currentWindow
                    )
                    if (files.isNotEmpty()) {
                        val f = files[0]
                        mainView.openTab(Lookup(f.name, FileLoader.fromFile(f)))
                    }
                }
                "About" action { About().dialog() }
                "Quit" action { mainView.close() }
            }
            "View" menu {
                "Search mods" action { mainView.selectOrOpenTab<ModSearch>() }
                "Language editor" action { mainView.selectOrOpenTab<LanguageEditor>() }
                "Create recipes" action { mainView.selectOrOpenTab<RecipeCreator>() }
                "Mapping viewer" action { mainView.selectOrOpenTab<MappingViewer>() }
            }
        }
    }
}