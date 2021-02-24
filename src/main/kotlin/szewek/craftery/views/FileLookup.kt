package szewek.craftery.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.craftery.cfapi.CurseforgeAPI
import szewek.craftery.layout.CenteredColumn
import szewek.craftery.layout.View
import szewek.craftery.util.Downloader
import szewek.craftery.util.FileLoader
import szewek.craftery.lookup.*
import szewek.craftery.mcdata.Modpack
import szewek.craftery.mcdata.ScanInfo
import java.util.zip.ZipInputStream

class FileLookup(
    name: String,
    private val loader: FileLoader,
    private val modpack: Boolean = false
): View("Lookup: $name") {
    private val loadingState = mutableStateOf(0f)
    private val message = mutableStateOf("")
    private val lookups: List<ModLookup<*>> = listOf(
        ListResourceData(),
        DetectCapabilities(),
        StaticFields(),
        SusLazyOptionals(),
        ListAllTags()
    )
    private val index = mutableStateOf(0)

    init {
        GlobalScope.launch { processLookups() }
    }

    @Composable
    override fun content() {
        if (loadingState.value != -1f) {
            CenteredColumn(Modifier.fillMaxSize()) {
                Text(message.value)
                LinearProgressIndicator(loadingState.value)
            }
        } else {
            Row {
                Column(Modifier.width(200.dp)) {
                    val bgBase = MaterialTheme.colors.onSurface
                    val bgSelected = bgBase.copy(0.25f)
                    val bgHover = bgBase.copy(0.2f)
                    val bgSelectedHover = bgBase.copy(0.4f)
                    lookups.forEachIndexed { i, l ->
                        val hover = remember { mutableStateOf(false) }
                        val bg = remember { derivedStateOf {
                            if (index.value == i) {
                                if (hover.value) bgSelectedHover else bgSelected
                            } else {
                                if (hover.value) bgHover else Color.Transparent
                            }
                        } }
                        Box(Modifier.background(bg.value, MaterialTheme.shapes.medium)
                            .clickable { index.value = i }
                            .pointerMoveFilter(onEnter = { hover.value = true; false }, onExit = { hover.value = false; false })) {
                            Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
                                Text(l.title, Modifier.weight(1f))
                                Text(
                                    l.list.size.toString(),
                                    Modifier.background(MaterialTheme.colors.primary, CircleShape).padding(6.dp, 2.dp),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Column(Modifier.fillMaxWidth()) {
                    ProvideTextStyle(TextStyle(fontSize = 12.sp)) {
                        lookups[index.value].content()
                    }
                }
            }
        }
    }

    private fun updateProgress(a: Long, max: Long) {
        loadingState.value = a.toFloat() / max
    }

    private fun updateMessage(msg: String) {
        message.value = msg
    }

    private fun processLookups() {
        val si = ScanInfo()
        if (modpack) {
            updateMessage("Downloading modpack...")
            updateProgress(0, 1)
            val fi = loader.load(::updateProgress)
            updateMessage("Reading manifest...")
            updateProgress(0, 1)
            val files = Modpack.readManifest(ZipInputStream(fi))
            val l = files.size
            files.forEachIndexed { i, (pid, fid) ->
                updateMessage("Getting file URL [$i / $l]...")
                updateProgress(0, 1)
                val murl = CurseforgeAPI.downloadURL(pid, fid)
                if (!murl.endsWith(".jar")) { return@forEachIndexed }
                val mname = murl.substringAfterLast('/')
                updateMessage("Downloading $mname...")
                val mf = Downloader.downloadFile(murl, ::updateProgress)
                updateMessage("Scanning $mname...")
                si.scanArchive(ZipInputStream(mf))
            }
        } else {
            updateMessage("Downloading file...")
            updateProgress(0, 1)
            val fi = loader.load(::updateProgress)
            updateMessage("Scanning classes...")
            updateProgress(0, 1)
            si.scanArchive(ZipInputStream(fi))
        }

        updateMessage("Gathering results...")
        updateProgress(2, 3)
        for (l in lookups) {
            l.lazyGather(si)
        }
        updateProgress(-3, 3)
    }
}