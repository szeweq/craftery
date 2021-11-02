package szeweq.craftery.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import szeweq.craftery.cfapi.*
import szeweq.craftery.layout.*
import szeweq.craftery.util.providesMerged
import szeweq.desktopose.combobox.ComboBox
import szeweq.desktopose.core.UseScopeText
import szeweq.desktopose.core.withProviders
import szeweq.desktopose.hover.hover

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
        topBar = topBar
    ) {
        val itemMod = Modifier.fillMaxWidth().height(64.dp)
        ScrollableColumn {
            // Prevent displaying out-of-bounds item layouts
            if (!modlist.isEmpty()) items(modlist.size, this@ModSearch::getSlugFromList) {
                if (it >= modlist.size) return@items
                val item = modlist[it]
                itemBox(item)
            } else {
                item {
                    Box(itemMod,
                        contentAlignment = Alignment.Center,
                        content = UseScopeText(if (progress.isActive) "Searching..." else "Empty")
                    )
                }
            }
        }
    }

    private fun getSlugFromList(i: Int): Any = if (i < modlist.size) modlist[i].slug else Unit

    @Composable
    private fun itemBox(item: AddonSearch) {
        Box(Modifier
            .clickable { ViewManager.open(AddonInfo(item)) }
            .hover(shape = MaterialTheme.shapes.medium)
            .padding(4.dp)
        ) {
            val attachment = remember(item, item::defaultAttachment)
            if (attachment != null) ImageURL(attachment.thumbnailUrl, item.name, Modifier.size(60.dp))
            Column(Modifier.padding(start = 64.dp)) {
                Row {
                    Text(item.name, Modifier.weight(1f, true), fontWeight = FontWeight.Bold)
                    Text("Downloads: ${item.downloadCount}", fontSize = 12.sp)
                }
                Text(item.slug, fontSize = 12.sp)
                Text(item.summary)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private val topBar = @Composable {
        val (text, setText) = search
        Card(Modifier.padding(12.dp, 4.dp)) {
            Row(Modifier.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                withProviders(LocalTextStyle providesMerged TextStyle(fontSize = 12.sp)) {
                    ComboBox(typeName.value, typeId.component2(), "Mod" to 6, "Modpack" to 4471)
                }
                SimpleTextField(
                    text, setText,
                    Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp)
                        .onKeyEvent { if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) findMods(); false }
                )
                IconButton(::findMods, enabled = search.value.isNotEmpty()) {
                    Icon(Icons.Default.Search, "Search")
                }
            }
        }
    }

    private fun findMods() {
        if (search.value.isEmpty()) {
            return
        }
        progress.setIndeterminate()
        val s = search.value
        val tid = typeId.value
        viewScope.launch(Dispatchers.IO) {
            modlist.clear()
            val a = CFAPI.findAddons(s, tid).await()
            modlist.addAll(a)
            title.value = "Search: $s"
            progress.setFinished()
        }
    }
}