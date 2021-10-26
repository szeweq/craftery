package szeweq.craftery

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.singleWindowApplication
import szeweq.craftery.layout.*
import szeweq.craftery.util.logTime
import szeweq.craftery.views.*
import szeweq.desktopose.core.*
import szeweq.desktopose.hover.DesktopButton
import szeweq.desktopose.hover.hover

fun startApp() =
    singleWindowApplication(visible = true, title = Craftery.APP_TITLE, content = app)

/**
 * Main composable function for an app. It saves bytecode size this way (Kotlin generates objects for functions).
 */
private val app: @Composable FrameWindowScope.() -> Unit = {
    AppTheme {
        Scaffold(
            topBar = { topBar() }
        ) {
            val v = ViewManager.active
            key(v) {
                if (v == null) {
                    window.title = Craftery.APP_TITLE
                    welcome()
                } else logTime("Update view [${v.javaClass.name}]") {
                    window.title = "${v.title.value} - ${Craftery.APP_TITLE}"
                    v.content()
                }
            }
        }
    }
}

@Composable
fun topBar() {
    TopAppBar(Modifier.height(32.dp)) {
        TabsView(Modifier.fillMaxHeight().weight(1f), ViewManager.views)
        Box(Modifier.fillMaxHeight().requiredWidth(24.dp).padding(vertical = 6.dp)) {
            val menuToggle = rememberInitialState(false)
            val iconSize = 24.dp
            val dismiss = menuToggle.bind(false)
            withProviders(
                LocalDismissMethod provides dismiss
            ) {
                DropdownMenu(menuToggle.value, dismiss) {
                    menuContent()
                }
            }
            IconButton(menuToggle.bind(true)) {
                Icon(Icons.Default.Menu, "Menu", Modifier.size(iconSize))
            }
        }
    }
}

@Composable
fun menuContent() {
    val h = 32.dp
    val menuPadding = PaddingValues(horizontal = 8.dp)
    val mod = Modifier.hover().heightIn(min = h, max = h)
    ProvideTextStyle(MaterialTheme.typography.caption) {
        for ((text, fn) in menuActions) {
            DropdownMenuItem(
                fn and LocalDismissMethod.current,
                mod,
                contentPadding = menuPadding,
                content = UseScopeText(text, style = LocalTextStyle.current)
            )
        }
    }

}

val menuActions: Array<Pair<String, () -> Unit>> = arrayOf(
    "Mod search" to ViewManager.selectOrOpenAction<ModSearch>(),
    "Language editor" to ViewManager.selectOrOpenAction<LanguageEditor>(),
    "Recipe Creator (WIP)" to ViewManager.selectOrOpenAction<RecipeCreator>(),
    "Performance" to ViewManager.selectOrOpenInstanceAction(PerformanceView),
    "About" to ViewManager.selectOrOpenInstanceAction(About)
)

@Composable
fun welcome() {
    CenteredColumn(ModifierMaxSize) {
        TextH5("What would you like to do?", Modifier.padding(8.dp))
        val mod = Modifier.padding(2.dp).width(200.dp)
        mainButton(ViewManager.selectOrOpenAction<ModSearch>(), mod, Icons.Default.Search, "Search for mods/modpacks")
        mainButton(ViewManager.selectOrOpenAction<LanguageEditor>(), mod, Icons.Default.Language, "Open language editor")
    }
}

@Composable
fun mainButton(onClick: () -> Unit, modifier: Modifier, icon: ImageVector, text: String) {
    DesktopButton(
        onClick,
        modifier,
    ) {
        CenteredColumn {
            Icon(icon, text)
            Text(text, Modifier.padding(top = 4.dp), textAlign = TextAlign.Center)
        }
    }
}
