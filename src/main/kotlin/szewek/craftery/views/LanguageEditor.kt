package szewek.craftery.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.craftery.layout.View
import szewek.craftery.layout.defaultScrollbarOnDark
import szewek.craftery.layout.hover
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class LanguageEditor: View("Language Editor") {
    private val chooser = JFileChooser()
    private val lang = mutableStateListOf<TranslationKeyValue>()

    class TranslationKeyValue(val key: String, val orig: String) {
        val trans = mutableStateOf("")
    }

    init {
        chooser.fileFilter = FileNameExtensionFilter("JSON File", "json")
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun content() = Scaffold(topBar = {
        Row(Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Button({
                val f = chooseJSON()
                if (f != null) GlobalScope.launch(Dispatchers.IO) { loadFile(f) }
            }) { Text("Load original file") }
            Button({
                val f = chooseJSON(true)
                if (f != null) GlobalScope.launch(Dispatchers.IO) { saveTranslation(f) }
            }) { Text("Save translations") }
        }
    }) {
        Box {
            val state = rememberLazyListState()
            val onHover = MaterialTheme.colors.onSurface.copy(0.2f)
            LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state = state) {
                items(lang) {
                    Box(Modifier.fillMaxWidth().hover(onHover, MaterialTheme.shapes.medium)
                    ) {
                        Column {
                            Text(it.key, Modifier.padding(2.dp))
                            Row {
                                val mod = Modifier.weight(0.5f).padding(2.dp)
                                val (trans, setTrans) = it.trans
                                ProvideTextStyle(TextStyle(fontSize = 12.sp)) {
                                    Column(mod) {
                                        Text("Original", fontWeight = FontWeight.Bold)
                                        Text(it.orig)
                                    }
                                    Column(mod) {
                                        Text("Translated", fontWeight = FontWeight.Bold)
                                        BasicTextField(
                                            trans, setTrans,
                                            Modifier.fillMaxWidth().background(onHover, MaterialTheme.shapes.small).padding(4.dp),
                                            textStyle = TextStyle(Color.White),
                                            cursorBrush = SolidColor(Color.White),
                                            singleLine = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            VerticalScrollbar(
                rememberScrollbarAdapter(state, lang.size, 72.dp),
                Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                defaultScrollbarOnDark
            )
        }
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
            val o = GSON.newJsonReader(it)
            o.beginObject()
            while (o.hasNext()) {
                val k = o.nextName()
                val v = o.nextString()
                l += TranslationKeyValue(k, v)
            }
            o.endObject()
            lang.clear()
            lang.addAll(l)
        }
    }

    private fun saveTranslation(f: File) {
        try {
            f.writer().use { w ->
                val jw = GSON.newJsonWriter(w)
                jw.beginObject()
                val l = lang.sortedBy { it.key }
                for (t in l) {
                    val trans = t.trans.value
                    if (trans.isNotEmpty()) {
                        jw.name(t.key).value(trans)
                    }
                }
                jw.endObject()
            }
        } catch (e: Exception) {
            println("ERROR WHILE SAVING!")
            e.printStackTrace()
        }

    }

    companion object {
        val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
    }
}