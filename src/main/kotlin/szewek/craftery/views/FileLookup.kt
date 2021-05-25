package szewek.craftery.views

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kittinunf.fuel.core.ProgressCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import szewek.craftery.cfapi.CurseforgeAPI
import szewek.craftery.layout.*
import szewek.craftery.lookup.*
import szewek.craftery.mcdata.Modpack
import szewek.craftery.mcdata.ScanInfo
import szewek.craftery.util.Downloader
import szewek.craftery.util.FileLoader
import szewek.craftery.util.bindValue
import java.util.zip.ZipInputStream

class FileLookup(
    name: String,
    private val loader: FileLoader,
    private val modpack: Boolean = false
): View("Lookup: $name") {
    private val message = mutableStateOf("")
    private val lookups: List<ModLookup<*>> = listOf(
        ListResourceData(),
        DetectCapabilities(),
        StaticFields(),
        SusLazyOptionals(),
        ListAllTags()
    )
    private val index = mutableStateOf(0)
    private val currentLookup = derivedStateOf { lookups[index.value] }

    init {
        viewScope.launch { processLookups() }
    }

    @Composable
    override fun content() {
        if (progress.isActive()) {
            CenteredColumn(Modifier.fillMaxSize()) {
                Text(message.value)
                LinearIndicator(progress)
            }
        } else {
            Row {
                sideList()
                Column(Modifier.fillMaxWidth()) {
                    ProvideTextStyle(TextStyle(fontSize = 12.sp)) {
                        currentLookup.value.content()
                    }
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
                fontSize = 12.sp
            )
        }
    }

    private fun updateMessage(msg: String) {
        message.value = msg
    }

    private fun processLookups() {
        val si = ScanInfo()
        val updateProgress: ProgressCallback = progress::setFraction
        if (modpack) {
            updateMessage("Downloading modpack...")
            progress.setIndeterminate()
            val fi = loader.load(updateProgress)
            updateMessage("Reading manifest...")
            progress.setIndeterminate()
            val files = Modpack.readManifest(ZipInputStream(fi))
            val l = files.size
            files.forEachIndexed { i, (pid, fid) ->
                updateMessage("Getting file URL [$i / $l]...")
                progress.setIndeterminate()
                val murl = CurseforgeAPI.downloadURL(pid, fid)
                if (!murl.endsWith(".jar")) { return@forEachIndexed }
                val mname = murl.substringAfterLast('/')
                updateMessage("Downloading $mname...")
                val mf = Downloader.downloadFile(murl, updateProgress)
                updateMessage("Scanning $mname...")
                si.scanArchive(ZipInputStream(mf))
            }
        } else {
            updateMessage("Downloading file...")
            progress.setIndeterminate()
            val fi = loader.load(updateProgress)
            updateMessage("Scanning classes...")
            progress.setIndeterminate()
            si.scanArchive(ZipInputStream(fi))
        }

        updateMessage("Gathering results...")
        updateProgress(2, 3)
        for (l in lookups) {
            l.lazyGather(si)
        }
        progress.setFinished()
    }
}