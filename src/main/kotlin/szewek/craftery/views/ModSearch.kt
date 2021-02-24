package szewek.craftery.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.craftery.cfapi.AddonSearch
import szewek.craftery.cfapi.CurseforgeAPI
import szewek.craftery.layout.ComboBox
import szewek.craftery.layout.View
import szewek.craftery.layout.ViewManager
import szewek.craftery.layout.defaultScrollbarOnDark

class ModSearch: View("Search mods") {
    private val search = mutableStateOf("")
    private val modlist = mutableStateListOf<AddonSearch>()
    private val typeId = mutableStateOf(6)
    private val typeName = derivedStateOf {
        when (typeId.value) { 6 -> "Mod" 4471 -> "Modpack" else -> "UNKNOWN" }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun content() = Scaffold(
        topBar = { topBar() }
    ) {
        Box {
            val state = rememberLazyListState()
            val onHover = MaterialTheme.colors.onSurface.copy(0.2f)
            LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state = state) {
                itemsIndexed(modlist) { _, item ->
                    val hoverBg = remember { mutableStateOf(Color.Transparent) }
                    Box(Modifier
                        .clickable { ViewManager.open(AddonInfo(item)) }
                        .background(hoverBg.value, MaterialTheme.shapes.medium)
                        .pointerMoveFilter(
                            onEnter = {hoverBg.value = onHover; false},
                            onExit = {hoverBg.value = Color.Transparent; false}
                        )
                    ) {
                        Column(Modifier.padding(4.dp)) {
                            Row {
                                Text(item.name, Modifier.weight(1f, true), fontWeight = FontWeight.Bold)
                                Text("Downloads: ${item.downloadCount}", fontSize = 12.sp)
                            }
                            Text(item.slug, fontSize = 12.sp)
                            Text(item.summary)
                        }
                    }
                }
            }
            VerticalScrollbar(
                rememberScrollbarAdapter(state, modlist.size, 64.dp),
                Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                defaultScrollbarOnDark
            )
        }
    }

    @Composable
    private fun topBar() = Row(Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        val (text, setText) = search
        OutlinedTextField(text, setText, Modifier.padding(start = 2.dp, end = 2.dp, bottom = 4.dp).weight(1f, true), placeholder = { Text("Search...") }, singleLine = true)
        ComboBox(typeName.value, typeId, "Mod" to 6, "Modpack" to 4471)
        Button(::findMods) {
            Text("Search")
            Icon(Icons.Default.Search, "Search")
        }
    }

    private fun findMods() {
        if (search.value.isEmpty()) {
            return
        }
        val s = search.value
        val tid = typeId.value
        GlobalScope.launch {
            val a = CurseforgeAPI.findAddons(s, tid)
            modlist.clear()
            modlist.addAll(a)
        }
    }
}