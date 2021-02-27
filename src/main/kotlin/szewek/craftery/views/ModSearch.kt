package szewek.craftery.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import szewek.craftery.layout.*

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
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp), state = state) {
                // Prevent displaying out-of-bounds item layouts
                if (!modlist.isEmpty()) items(modlist.size, this@ModSearch::getSlugFromList) {
                    if (it >= modlist.size) return@items
                    val item = modlist[it]
                    Box(Modifier
                        .clickable { ViewManager.open(AddonInfo(item)) }
                        .hover(onHover, shape = MaterialTheme.shapes.medium)
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
                } else {
                    item { Box(Modifier.fillMaxWidth().height(64.dp), contentAlignment = Alignment.Center) { Text(if (progress.isActive()) "Searching..." else "Empty") } }
                }
            }
            VerticalScrollbar(
                rememberScrollbarAdapter(state, modlist.size, 64.dp),
                Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                defaultScrollbarOnDark
            )
        }
    }

    private fun getSlugFromList(i: Int): Any = if (i < modlist.size) modlist[i].slug else Unit

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
        progress.setIndeterminate()
        val s = search.value
        val tid = typeId.value
        GlobalScope.launch {
            modlist.clear()
            val a = CurseforgeAPI.findAddons(s, tid)
            modlist.addAll(a)
            progress.setFinished()
        }
    }
}