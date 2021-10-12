package szeweq.craftery.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import szeweq.craftery.cfapi.CFAPI
import szeweq.craftery.layout.*
import szeweq.craftery.lookup.*
import szeweq.craftery.mcdata.Modpack
import szeweq.craftery.net.Downloader
import szeweq.craftery.scan.ScanInfo
import szeweq.craftery.util.FileLoader
import szeweq.craftery.util.bindValue
import java.io.InputStream
import java.util.zip.ZipInputStream

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
    private val loading = mutableStateOf(true)
    private val index = mutableStateOf(0)
    private val currentLookup = derivedStateOf { lookups[index.value] }
    private val downloadProgress = MessageProgressState()
    private val scanProgress = MessageProgressState()

    init {
        viewScope.launch { processLookups() }
    }

    @Composable
    override fun content() {
        if (loading.value) {
            CenteredColumn(Modifier.fillMaxSize()) {
                Text("Loading lookups...", Modifier.padding(8.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                val mod = Modifier.fillMaxWidth(0.75f).padding(4.dp)
                if (downloadProgress.isActive())
                    ProgressCard(downloadProgress, mod)
                ProgressCard(scanProgress, mod)
            }
        } else {
            Row {
                sideList()
                ProvideTextStyle(TextStyle(fontSize = 12.sp)) {
                    currentLookup.value.content()
                }
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
            lookups.forEachIndexed { i, l ->
                val (hover, setHover) = remember { mutableStateOf(false) }
                val bg = remember(index.value, hover) { derivedStateOf {
                    if (index.value == i) {
                        if (hover) bgSelectedHover else bgSelected
                    } else {
                        if (hover) bgHover else Color.Transparent
                    }
                } }
                Box(Modifier
                    .hoverState(setHover)
                    .background(bg.value, MaterialTheme.shapes.medium)
                    .clickable(onClick = index.bindValue(i))
                ) { sideListItem(l) }
            }
        }
    }

    @Composable
    private fun sideListItem(l: ModLookup<*>) {
        Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
            Text(l.title, Modifier.weight(1f))
            Text(
                l.list.size.toString(),
                Modifier.background(MaterialTheme.colors.primary, CircleShape).padding(6.dp, 2.dp),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 12.sp
            )
        }
    }

    private suspend fun processLookups() {
        val si = ScanInfo()
        loading.value = true
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
                val mname = murl.substringAfterLast('/')
                downloadProgress.message = "Downloading [$i / $total] $mname..."
                val mf = Downloader.downloadFile(murl.replace(" ", "%20"), downloadProgress).await()
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
            scanProgress.message = "Scanning [$i / $total] $name..."
            scanProgress.accept(i.toLong(), total)
            ZipInputStream(input).use(si::scanArchive)
        }
        gather(si)
        loading.value = false
        progress.setFinished()
    }

    private fun gather(si: ScanInfo) {
        scanProgress.value = 0F
        var li = 0L
        val ls = lookups.size.toLong()
        for (l in lookups) {
            scanProgress.message = "Gathering results (${l.title})..."
            l.lazyGather(si)
            scanProgress.accept(++li, ls)
        }
        scanProgress.setFinished()
    }
}