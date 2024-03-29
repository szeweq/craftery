package szeweq.craftery.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szeweq.craftery.cfapi.AddonSearch
import szeweq.craftery.layout.*
import szeweq.craftery.util.FileLoader
import szeweq.desktopose.core.UseScopeText
import szeweq.desktopose.core.providesMerged
import szeweq.desktopose.core.withProviders
import szeweq.desktopose.hover.DesktopButton
import szeweq.desktopose.hover.hover
import szeweq.kt.KtUtil

class AddonInfo(private val addon: AddonSearch): View(addon.name) {

    @Composable
    override fun content() = Column(ModifierMaxSize.padding(4.dp)) {
        Row {
            val img = addon.defaultAttachment()
            if (img != null) {
                ImageURL(img.thumbnailUrl, "Icon", Modifier.size(64.dp).padding(end = 4.dp))
            }
            Column(Modifier.weight(1f, true)) {
                Text(addon.name, fontWeight = FontWeight.Bold)
                Text(addon.slug, fontSize = 12.sp)
            }
            val lf = addon.latestFile()
            if (lf != null) {
                DesktopButton({
                    ViewManager.open(FileLookup(lf.fileName, FileLoader.fromURL(lf.downloadUrl), addon.categorySection.packageType != 6))
                }, content = UseScopeText("Lookup"))
            }
        }
        withProviders(LocalTextStyle providesMerged TextStyle(fontSize = 12.sp)) {
            infoRow("Authors", addon.authors.joinToString { it.name })
            infoRow("Summary", addon.summary)
            infoRow("Download count", addon.downloadCount.toString())
            Row {
                Text("Website URL", Modifier.requiredWidth(120.dp), fontWeight = FontWeight.Bold)
                urlText(addon.websiteUrl)
            }
            Card(ModifierMaxSize.padding(2.dp)) {
                Column {
                    val mod = Modifier.fillMaxWidth().hover()
                    for (f in addon.latestFiles) {
                        Box(mod, contentAlignment = Alignment.CenterStart) {
                            Column(Modifier.padding(2.dp)) {
                                Text(f.fileName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                infoRow("Date", f.fileDate.toString())
                                infoRow("Size", KtUtil.lengthInBytes(f.fileLength.toLong()))
                                infoRow("Versions", f.gameVersion.joinToString())
                            }
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