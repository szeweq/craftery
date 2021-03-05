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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.craftery.cfapi.AddonSearch
import szewek.craftery.cfapi.CurseforgeAPI
import szewek.craftery.cfapi.default
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
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp), state = state) {
                // Prevent displaying out-of-bounds item layouts
                if (!modlist.isEmpty()) items(modlist.size, this@ModSearch::getSlugFromList) {
                    if (it >= modlist.size) return@items
                    val item = modlist[it]
                    itemBox(item)
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
    private fun itemBox(item: AddonSearch) {
        Box(Modifier
            .clickable { ViewManager.open(AddonInfo(item)) }
            .hover(LocalHoverColor.current, shape = MaterialTheme.shapes.medium)
            .padding(4.dp)
        ) {
            Row {
                val attachment = remember(item) { item.attachments.default() }
                if (attachment != null) ImageURL(attachment.thumbnailUrl, item.name, Modifier.size(60.dp).padding(end = 4.dp))
                Column {
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

    @Composable
    private fun topBar() {
        val (text, setText) = search
        OutlinedTextField(
            text, setText,
            Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                .fillMaxWidth()
                .onKeyEvent { if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) findMods(); false },
            placeholder = { Text("Search...") },
            leadingIcon = {
                ComboBox(typeName.value, typeId, "Mod" to 6, "Modpack" to 4471)
            },
            trailingIcon = {
                IconButton(::findMods, enabled = search.value.isNotEmpty()) {
                    Icon(Icons.Default.Search, "Search")
                }
            },
            singleLine = true
        )
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