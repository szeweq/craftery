package szewek.craftery

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szewek.craftery.layout.CenteredColumn
import szewek.craftery.layout.TabsView
import szewek.craftery.layout.ViewManager
import szewek.craftery.layout.hover
import szewek.craftery.views.*

internal val colorsDark = darkColors(
    primary = Color(0xff394739),
    onPrimary = Color.White
)

fun main() = Window(title = "Craftery") {
    DesktopMaterialTheme(
        colors = colorsDark
    ) {
        Scaffold(
            topBar = {
                TopAppBar(Modifier.height(40.dp)) {
                    Row(Modifier.fillMaxHeight().weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        TabsView(ViewManager.views)
                    }
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

@Composable
fun menuContent(dismiss: () -> Unit) {
    val hoverColor = MaterialTheme.colors.onSurface.copy(0.2f)
    for (action in menuActions) {
        DropdownMenuItem({ action.fn(); dismiss() }, Modifier.hover(hoverColor)) { Text(action.text) }
    }
}

class MenuAction(val text: String, val fn: () -> Unit)
val menuActions = arrayOf(
    MenuAction("Mod Search") { ViewManager.selectOrOpen<ModSearch>() },
    MenuAction("Language editor") { ViewManager.selectOrOpen<LanguageEditor>() },
    MenuAction("Mappings viewer (WIP)") { ViewManager.selectOrOpen<MappingViewer>() },
    MenuAction("Recipe Creator (WIP)") { ViewManager.selectOrOpen<RecipeCreator>() },
    MenuAction("About") { ViewManager.selectOrOpen<About>() }
)

@Composable
fun welcome() {
    CenteredColumn(Modifier.fillMaxSize(1.0f)) {
        Text("What would you like to do?", Modifier.padding(8.dp), fontSize = 24.sp)
        val mod = Modifier.padding(2.dp)
        Button({ ViewManager.selectOrOpen<ModSearch>() }, mod) { Text("Search for mods/modpacks") }
        Button({ ViewManager.selectOrOpen<LanguageEditor>() }, mod) { Text("Open language editor") }
    }
}
