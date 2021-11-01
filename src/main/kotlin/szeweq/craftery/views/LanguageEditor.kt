package szeweq.craftery.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import szeweq.craftery.layout.*
import szeweq.craftery.util.JsonUtil
import szeweq.desktopose.hover.DesktopButton
import szeweq.desktopose.hover.LocalHoverColor
import szeweq.desktopose.hover.hover
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class LanguageEditor: View("Language Editor") {
    private val chooser = JFileChooser()
    private val lang = mutableStateListOf<TranslationKeyValue>()

    class TranslationKeyValue(val key: String, val orig: String) {
        var trans by mutableStateOf("")
    }

    init {
        chooser.fileFilter = FileNameExtensionFilter("JSON File", "json")
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun content() = Scaffold(topBar = { topBar() }) {
        ScrollableColumn {
            items(lang) {
                Column(Modifier.fillMaxWidth().hover(shape = MaterialTheme.shapes.medium)) {
                    Text(it.key, Modifier.padding(2.dp))
                    Row {
                        val mod = Modifier.weight(0.5f).padding(2.dp)
                        ProvideTextStyle(TextStyle(fontSize = 12.sp)) {
                            Column(mod) {
                                Text("Original", fontWeight = FontWeight.Bold)
                                Text(it.orig)
                            }
                            Column(mod) {
                                Text("Translated", fontWeight = FontWeight.Bold)
                                SimpleTextField(
                                    it.trans,
                                    it::trans::set,
                                    Modifier.fillMaxWidth(),
                                    background = LocalHoverColor.current
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun topBar() = Row(
        Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val mod = Modifier.padding(4.dp)
        DesktopButton({
            val f = chooseJSON()
            if (f != null) viewScope.launch(Dispatchers.IO) { loadFile(f) }
        }, mod) { Text("Load original file") }
        DesktopButton({
            val f = chooseJSON(true)
            if (f != null) viewScope.launch(Dispatchers.IO) { saveTranslation(f) }
        }, mod) { Text("Save translations") }
    }

    private fun chooseJSON(save: Boolean = false) = with(chooser) {
        if (save) { showSaveDialog(null) } else { showOpenDialog(null) }
        val f = selectedFile
        selectedFile = null
        f
    }

    private fun loadFile(f: File) {
        f.reader().use {
            val l = mutableListOf<TranslationKeyValue>()
            val o = JsonUtil.mapper.readTree(it) as ObjectNode
            for ((k, v) in o.fields()) {
                l += TranslationKeyValue(k, v.asText())
            }
            lang.clear()
            lang.addAll(l)
        }
    }

    private fun saveTranslation(f: File) {
        try {
            f.writer().use { w ->
                val jg = writer.createGenerator(w)
                jg.writeStartObject()
                val l = lang.sortedBy { it.key }
                for (t in l) {
                    val trans = t.trans
                    if (trans.isNotEmpty()) {
                        jg.writeStringField(t.key, trans)
                    }
                }
                jg.writeEndObject()
            }
        } catch (e: Exception) {
            println("ERROR WHILE SAVING!")
            e.printStackTrace()
        }

    }

    companion object {
        val writer: ObjectWriter = JsonUtil.mapper.writerWithDefaultPrettyPrinter()
    }
}
