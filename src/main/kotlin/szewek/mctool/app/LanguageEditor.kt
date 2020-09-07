package szewek.mctool.app

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import javax.json.Json
import javax.json.JsonString
import javax.json.stream.JsonGenerator
import javax.json.stream.JsonGeneratorFactory

class LanguageEditor: View("Language Editor") {
    override val root = BorderPane()
    private val lang: ObservableList<TranslationKeyValue> = FXCollections.observableArrayList()

    init {
        root.apply {
            top = toolbar {
                button("Load original file").setOnAction {
                    chooseJSON("Choose JSON file") {
                        loadFile(it[0])
                    }
                }
                button("Save translations").setOnAction {
                    chooseJSON("Save translated file", FileChooserMode.Save) {
                        saveTranslation(it[0])
                    }
                }
            }
            center = tableview(lang) {
                readonlyColumn("Key", TranslationKeyValue::key).pctWidth(20)
                readonlyColumn("Original", TranslationKeyValue::orig).pctWidth(40)
                column<TranslationKeyValue, String>("Translation") { it.value.transProp }.makeEditable().remainingWidth()
                smartResize()
            }
        }
    }

    private fun chooseJSON(title: String, mode: FileChooserMode = FileChooserMode.Single, fn: (List<File>) -> Unit) {
        val files = chooseFile(
                title,
                arrayOf(FileChooser.ExtensionFilter("JSON File", "*.json")),
                mode = mode,
                owner = currentWindow
        )
        if (files.isNotEmpty()) {
            fn(files)
        }
    }

    class TranslationKeyValue(val key: String, val orig: String) {
        var transProp = SimpleStringProperty("")
        var trans: String by transProp
    }

    private fun loadFile(f: File) {
        f.inputStream().use {
            val l = mutableListOf<TranslationKeyValue>()
            val obj = Json.createReader(it).readObject()
            for ((k, jv) in obj) {
                if (jv is JsonString) {
                    l += TranslationKeyValue(k, jv.string)
                }
            }
            lang.setAll(l)
        }
    }

    private fun saveTranslation(f: File) {
        f.outputStream().use { fos ->
            val jg = JGF.createGenerator(fos)
            jg.writeStartObject()
            val l = lang.sortedBy { it.key }
            for (t in l) {
                jg.write(t.key, t.trans)
            }
            jg.writeEnd()
        }
    }

    companion object {
        val JGF: JsonGeneratorFactory = Json.createGeneratorFactory(mapOf(
            JsonGenerator.PRETTY_PRINTING to true
        ))
    }
}