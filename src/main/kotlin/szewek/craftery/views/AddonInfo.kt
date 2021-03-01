package szewek.craftery.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szewek.craftery.cfapi.AddonSearch
import szewek.craftery.cfapi.default
import szewek.craftery.cfapi.latest
import szewek.craftery.layout.View
import szewek.craftery.layout.ViewManager
import szewek.craftery.util.FileLoader
import szewek.craftery.layout.ImageURL
import szewek.craftery.util.KtUtil

class AddonInfo(private val addon: AddonSearch): View(addon.name) {

    @Composable
    override fun content() = Column(Modifier.padding(4.dp).fillMaxSize()) {
        Row {
            val img = addon.attachments.default()
            if (img != null) {
                ImageURL(img.thumbnailUrl, "Icon", Modifier.size(64.dp).padding(end = 4.dp))
            }
            Column(Modifier.weight(1f, true)) {
                Text(addon.name, fontWeight = FontWeight.Bold)
                Text(addon.slug, fontSize = 12.sp)
            }
            val lookupText = remember { mutableStateOf("Lookup") }
            val enableLookup = remember { mutableStateOf(true) }
            Button({
                val lf = addon.latestFiles.latest()
                if (lf != null) {
                    ViewManager.open(FileLookup(lf.fileName, FileLoader.fromURL(lf.downloadUrl), addon.categorySection.packageType != 6))
                } else {
                    lookupText.value = "No files found"
                    enableLookup.value = false
                }
            }) { Text(lookupText.value) }
        }
        ProvideTextStyle(TextStyle(fontSize = 12.sp)) {
            infoRow("Authors", addon.authors.joinToString { it.name })
            infoRow("Summary", addon.summary)
            infoRow("Download count", addon.downloadCount.toString())
            Row {
                Text("Website URL", Modifier.requiredWidth(120.dp))
                urlText(addon.websiteUrl)
            }
            Divider(thickness = 1.dp)
            Column(Modifier.fillMaxSize().padding(2.dp)) {
                for (f in addon.latestFiles) {
                    Box {
                        Column(Modifier.padding(2.dp)) {
                            Text(f.fileName, fontWeight = FontWeight.Bold)
                            infoRow("Date", f.fileDate.toString())
                            infoRow("Size", KtUtil.lengthInBytes(f.fileLength.toLong()))
                            infoRow("Versions", f.gameVersion.joinToString())
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun infoRow(name: String, value: String) = Row {
        Text(name, Modifier.requiredWidth(120.dp), fontWeight = FontWeight.Bold)
        Text(value)
    }

    @Composable
    private fun urlText(url: String) {
        val text = AnnotatedString.Builder().apply {
            append(url)
            addStringAnnotation("URL", url, 0, url.length)
        }.toAnnotatedString()
        Text(text)
    }
}