package szewek.craftery

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szewek.craftery.layout.*
import szewek.craftery.util.logTime
import szewek.craftery.views.*

fun startApp() = logTime("App launch") {
    val win = AppWindow(title = Craftery.APP_TITLE)
    win.show(content = app)
}

private val app = @Composable {
    AppTheme {
        Scaffold(
            topBar = { topBar() }
        ) {
            val v = ViewManager.active
            key(v) {
                updateTitle(v?.title?.value)
                if (v == null) {
                    welcome()
                } else logTime("Update view [${v.javaClass.name}]") {
                    v.content()
                }
            }
        }
    }
}

@Composable
fun updateTitle(title: String?) {
    val w = LocalAppWindow.current
    SideEffect {
        val t = if (title == null || title.isBlank()) Craftery.APP_TITLE else "$title - ${Craftery.APP_TITLE}"
        w.setTitle(t)
    }
}

@Composable
fun topBar() {
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

@Composable
fun menuContent(dismiss: () -> Unit) {
    for ((text, fn) in menuActions) {
        DropdownMenuItem({ fn(); dismiss() }, Modifier.hover(LocalHoverColor.current)) { Text(text, fontSize = 14.sp) }
    }
}

val menuActions: Array<Pair<String, () -> Unit>> = arrayOf(
    "Mod search" to { ViewManager.selectOrOpen<ModSearch>() },
    "Language editor" to { ViewManager.selectOrOpen<LanguageEditor>() },
    "Recipe Creator (WIP)" to { ViewManager.selectOrOpen<RecipeCreator>() },
    "Time logs" to { ViewManager.selectOrOpenInstance(TimeLogViewer) },
    "About" to { ViewManager.selectOrOpenInstance(About) }
)

@Composable
fun welcome() {
    CenteredColumn(Modifier.fillMaxSize(1.0f)) {
        Text("What would you like to do?", Modifier.padding(8.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        val mod = Modifier.padding(2.dp)
        Button({ ViewManager.selectOrOpen<ModSearch>() }, mod) { Text("Search for mods/modpacks") }
        Button({ ViewManager.selectOrOpen<LanguageEditor>() }, mod) { Text("Open language editor") }
    }
}
