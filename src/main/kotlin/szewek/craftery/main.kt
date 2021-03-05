package szewek.craftery

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szewek.craftery.layout.*
import szewek.craftery.views.*
import javax.swing.UIManager

fun main() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        println("Unable to set system Look and Feel")
        e.printStackTrace()
    }

    Window(title = "Craftery") {
        AppTheme {
            Scaffold(
                topBar = {
                    TopAppBar(Modifier.height(40.dp)) {
                        TabsView(Modifier.fillMaxHeight().weight(1f), ViewManager.views)
                        Box(Modifier.fillMaxHeight().requiredWidth(28.dp).padding(vertical = 6.dp)) {
                            val menuToggle = remember { mutableStateOf(false) }
                            val iconSize = 28.dp
                            val dismiss = { menuToggle.value = false }

                            IconButton({ menuToggle.value = true }) {
                                Icon(Icons.Default.Menu, "Menu", Modifier.size(iconSize))
                            }
                            DropdownMenu(menuToggle.value, dismiss, offset = DpOffset(-iconSize, 0.dp)) {
                                menuContent(dismiss)
                            }
                        }
                    }
                }
            ) {
                val v = ViewManager.active
                if (v == null) welcome() else v.content()
            }
        }
    }
}

@Composable
fun menuContent(dismiss: () -> Unit) {
    for (action in menuActions) {
        DropdownMenuItem({ action.fn(); dismiss() }, Modifier.hover(LocalHoverColor.current)) { Text(action.text, fontSize = 14.sp) }
    }
}

class MenuAction(val text: String, val fn: () -> Unit)
val menuActions = arrayOf(
    "Mod search" { ViewManager.selectOrOpen<ModSearch>() },
    "Language editor" { ViewManager.selectOrOpen<LanguageEditor>() },
    "Mappings viewer (WIP)" { ViewManager.selectOrOpen<MappingViewer>() },
    "Recipe Creator (WIP)" { ViewManager.selectOrOpen<RecipeCreator>() },
    "About" { ViewManager.selectOrOpen<About>() }
)

private operator fun String.invoke(fn: () -> Unit) = MenuAction(this, fn)

@Composable
fun welcome() {
    CenteredColumn(Modifier.fillMaxSize(1.0f)) {
        Text("What would you like to do?", Modifier.padding(8.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        val mod = Modifier.padding(2.dp)
        Button({ ViewManager.selectOrOpen<ModSearch>() }, mod) { Text("Search for mods/modpacks") }
        Button({ ViewManager.selectOrOpen<LanguageEditor>() }, mod) { Text("Open language editor") }
    }
}
