package szewek.craftery.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ProgressCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.craftery.layout.ComboBox
import szewek.craftery.layout.View
import szewek.craftery.layout.defaultScrollbarOnDark
import szewek.craftery.util.Downloader

class MappingViewer: View("Mapping viewer (WIP)") {
    private val mavenURL = mutableStateOf("")
    private val mcpVersions = mutableStateListOf<String>()
    private val mappingVersions = mutableStateListOf<String>()
    private val mappingList = mutableStateListOf<Mapping>()
    private val selectedMcp = mutableStateOf("")
    private val selectedMapping = mutableStateOf("")

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun content() = Row {
        Column(Modifier.weight(0.25f)) {
            val (url, setUrl) = mavenURL
            OutlinedTextField(url, setUrl, placeholder = { Text("Maven URL") }, singleLine = true)
            Button({ if (url.isNotEmpty()) listVersions() }) { Text("Update") }

            val mcpMap = if (selectedMcp.value.isEmpty()) "MCP Mapping" else selectedMcp.value
            ComboBox(mcpMap, selectedMcp, mcpVersions)

            val mapVer = if(selectedMapping.value.isEmpty()) "Mapping version" else selectedMapping.value
            ComboBox(mapVer, selectedMapping, mappingVersions)

            val b = selectedMcp.value.isEmpty() or selectedMapping.value.isEmpty()
            Button({ if(!b) listMappings() }) { Text("Contruct list") }
        }
        Column(Modifier.weight(0.75f)) {
            Box {
                val state = rememberLazyListState()
                val onHover = MaterialTheme.colors.onSurface.copy(0.2f)
                ProvideTextStyle(TextStyle(fontSize = 12.sp)) {
                    LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state = state) {
                        itemsIndexed(mappingList) { _, item ->
                            val hoverBg = remember { mutableStateOf(Color.Transparent) }
                            Box(Modifier
                                .background(hoverBg.value, MaterialTheme.shapes.medium)
                                .pointerMoveFilter(
                                    onEnter = {hoverBg.value = onHover; false},
                                    onExit = {hoverBg.value = Color.Transparent; false}
                                )
                            ) {
                                Column(Modifier.padding(4.dp)) {
                                    Text(item.name, fontWeight = FontWeight.Bold)
                                    Text("Class: ${item.cl}")
                                    Text("Mapped: ${item.mapped}")
                                }
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    rememberScrollbarAdapter(state, mappingList.size, 64.dp),
                    Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    defaultScrollbarOnDark
                )
            }
        }
    }

    private fun listVersions() {
        val murl = mavenURL.value
        GlobalScope.launch {
            mavenGet(murl, "mcp_config/maven-metadata.xml") // Response XML
            mavenGet(murl, "mcp_snapshot/maven-metadata.xml")
        }
    }

    private fun listMappings() {
        val murl = mavenURL.value
        val progress: ProgressCallback = { _, _ -> }
        GlobalScope.launch {
            mavenPkg(murl, "mcp_config", selectedMcp.value, "zip", progress) // Unzip
            mavenPkg(murl, "mcp_snapshot", selectedMapping.value, "zip", progress)
        }
    }

    class Mapping(val name: String, val cl: String, val mapped: String)

    companion object {
        fun mavenGet(murl: String, path: String) = Fuel.get("$murl/de/oceanlabs/mcp/$path")
        fun mavenPkg(murl: String, name: String, version: String, ext: String, progress: ProgressCallback)
                = Downloader.downloadFile("$murl/de/oceanlabs/mcp/$name/$version/$name-$version.$ext", progress)
    }
}