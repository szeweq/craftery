package szewek.mctool.app

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
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
    val lang = FXCollections.observableArrayList<TranslationKeyValue>()

    init {
        root.apply {
            top = toolbar {
                button("Load original file").setOnAction {
                    val files = chooseFile(
                        "Choose JSON file",
                        arrayOf(FileChooser.ExtensionFilter("JSON File", "*.json")),
                        owner = currentWindow
                    )
                    if (files.isNotEmpty()) {
                        val f = files[0]
                        println("Selected file: " + f.absolutePath)
                        loadFile(f)
                    }
                }
                button("Save translations").setOnAction {
                    val files = chooseFile(
                        "Save translated file",
                        arrayOf(FileChooser.ExtensionFilter("JSON File", "*.json")),
                        mode = FileChooserMode.Save,
                        owner = currentWindow
                    )
                    if (files.isNotEmpty()) {
                        val f = files[0]
                        println("Selected file: " + f.absolutePath)
                        saveTranslation(f)
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

    class TranslationKeyValue(val key: String, val orig: String) {
        var transProp = SimpleStringProperty("")
        var trans: String by transProp
        fun transProperty() = transProp
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
        f.outputStream().use {
            val jg = JGF.createGenerator(it)
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