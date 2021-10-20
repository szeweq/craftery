package szeweq.craftery.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.future.await
import szeweq.craftery.cfapi.CFAPI
import szeweq.craftery.layout.*
import szeweq.craftery.lookup.*
import szeweq.craftery.mcdata.Modpack
import szeweq.craftery.net.Downloader
import szeweq.craftery.scan.ScanInfo
import szeweq.craftery.util.FileLoader
import szeweq.craftery.util.bind
import java.io.InputStream
import java.net.URLEncoder
import java.util.zip.ZipInputStream
import kotlin.time.measureTime

class FileLookup(
    private val filename: String,
    private val loader: FileLoader,
    private val modpack: Boolean = false
): View("Lookup: $filename") {
    private val lookups: List<ModLookup<*>> = listOf(
        ListResourceData(),
        DetectCapabilities(),
        StaticFields(),
        SusLazyOptionals(),
        ListAllTags(),
        ParseErrors()
    )
    private val checks = lookups.map { false }.toMutableStateList()
    private val workState = mutableStateOf(0)
    private val index = mutableStateOf(0)
    private val currentLookup = derivedStateOf { lookups[index.value] }
    private val downloadProgress = MessageProgressState()
    private val scanProgress = MessageProgressState()

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun content() {
        when (workState.value) {
            0 -> CenteredColumn(ModifierMaxSize) {
                TextH5("Select lookups to apply", Modifier.padding(8.dp))
                Card(Modifier.fillMaxWidth(0.75f).padding(4.dp)) { Column {
                    for (i in lookups.indices) {
                        Row(Modifier.fillMaxWidth().hover(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checks[i], { checks[i] = it }, Modifier.pointerHoverIcon(PointerIcon.Hand))
                            Column {
                                val l = lookups[i]
                                Text(l.title, Modifier.padding(vertical = 2.dp))
                                l.explain?.let { Text(it, fontSize = 12.sp) }
                            }
                        }
                    }
                    val enabled = remember { derivedStateOf { for (b in checks) if (b) return@derivedStateOf true; false } }
                    DesktopButton(
                        workState.bind(1),
                        Modifier.padding(vertical = 4.dp).align(Alignment.CenterHorizontally).fillMaxWidth(0.5f),
                        enabled = enabled.value,
                        content = ComposeScopeText("Continue")
                    )
                } }
            }
            1 -> {
                LaunchedEffect(workState.value) { processLookups() }
                CenteredColumn(ModifierMaxSize) {
                    Text("Loading lookups...", Modifier.padding(8.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    val mod = Modifier.fillMaxWidth(0.75f).padding(4.dp)
                    if (downloadProgress.isActive())
                        ProgressCard(downloadProgress, mod)
                    ProgressCard(scanProgress, mod)
                }
            }
            else -> Row {
                sideList()
                if (checks[index.value]) ProvideTextStyle(TextStyle(fontSize = 12.sp)) {
                     currentLookup.value.content()

                } else Box(ModifierMaxSize) { Text("This lookup is disabled!", Modifier.align(Alignment.Center)) }
            }
        }
    }

    @Composable
    private fun sideList() {
        Column(Modifier.width(200.dp)) {
            val bgBase = MaterialTheme.colors.onSurface
            val bgSelected = bgBase.copy(0.25f)
            val bgHover = LocalHoverColor.current
            val bgSelectedHover = bgBase.copy(0.4f)
            val hover = remember { lookups.map { false }.toMutableStateList() }
            lookups.forEachIndexed { i, l ->
                val bg = remember(index.value, hover[i]) {
                    if (index.value == i) {
                        if (hover[i]) bgSelectedHover else bgSelected
                    } else {
                        if (hover[i]) bgHover else Color.Transparent
                    }
                }
                Box(Modifier
                    .hoverState { hover[i] = it }
                    .background(bg, MaterialTheme.shapes.medium)
                    .clickable(onClick = index.bind(i))
                ) { sideListItem(i, l) }
            }
        }
    }

    @Composable
    private fun sideListItem(i: Int, l: ModLookup<*>) {
        Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
            Text(l.title, Modifier.weight(1f))
            if (checks[i]) Text(
                l.list.size.toString(),
                Modifier.background(MaterialTheme.colors.primary, CircleShape).padding(6.dp, 2.dp),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 12.sp
            )
        }
    }

    private suspend fun processLookups() {
        val si = ScanInfo()
        progress.setIndeterminate()
        var total = 1L
        val inputFlow: Flow<Pair<String, InputStream>> = if (modpack) flow {
            downloadProgress.message = "Downloading modpack..."
            downloadProgress.setIndeterminate()
            val fi = loader.load(downloadProgress).await()
            downloadProgress.message = "Reading manifest..."
            downloadProgress.setIndeterminate()
            val files = Modpack.readManifest(ZipInputStream(fi))
            total = files.size.toLong()
            var i = 0
            for ((pid, fid) in files) {
                downloadProgress.message = "Getting file URL [$i / $total]..."
                downloadProgress.value = 0F
                val murl = CFAPI.downloadURL(pid, fid).await()
                if (!murl.endsWith(".jar")) continue
                val ix = murl.lastIndexOf('/') + 1
                val mname = murl.substring(ix)
                downloadProgress.message = "Downloading [$i / $total] $mname..."
                val mf = Downloader.downloadFile(
                    murl.substring(0, ix) + URLEncoder.encode(mname, Charsets.UTF_8),
                    downloadProgress
                ).await()
                emit(mname to mf)
                i++
            }
            downloadProgress.setFinished()
        } else flow {
            downloadProgress.message = "Downloading file..."
            downloadProgress.setIndeterminate()
            val fi = loader.load(downloadProgress).await()
            emit(filename to fi)
            downloadProgress.setFinished()
        }
        inputFlow.collectIndexed { i, (name, input) ->
            val d = System.nanoTime()
            scanProgress.message = "Scanning [$i / $total] $name..."
            scanProgress.accept(i.toLong(), total)
            si.scanArchive(ZipInputStream(input))
            val du = (System.nanoTime() - d) / 1e6f
            println("Done [$name] in $du")
        }
        gather(si)
        workState.value = 2
        progress.setFinished()
    }

    private suspend fun gather(si: ScanInfo) {
        scanProgress.message = "Preparing lookups..."
        scanProgress.setIndeterminate()
        delay(1000)
        scanProgress.value = 0F
        var li = 0L
        val ls = checks.count { it }.toLong()
        for (i in lookups.indices) {
            if (checks[i]) {
                val l = lookups[i]
                scanProgress.message = "Gathering results (${l.title})..."
                l.lazyGather(viewScope, si)
                scanProgress.accept(++li, ls)
            }
        }
        scanProgress.setFinished()
    }
}