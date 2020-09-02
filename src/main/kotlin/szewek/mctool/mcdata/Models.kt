package szewek.mctool.mcdata

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.image.Image
import szewek.mctool.app.task.TaskFunc
import szewek.mctool.app.task.TaskManager
import tornadofx.task
import java.io.ByteArrayInputStream
import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern
import javax.json.Json
import javax.json.JsonString

object Models {
    val modelMap = mutableMapOf<String, ModelData>()
    val textures = mutableMapOf<String, Image>()
    val compileState = SimpleBooleanProperty(false)
    private val matchModel = Pattern.compile("^assets/[a-z]+/models")
    private val matchTex = Pattern.compile("^assets/[a-z]+/textures")

    fun compile(): TaskFunc {
        return {
            updateMessage("Compiling resources")
            val latch = CountDownLatch(2)
            val mt = task(func = compileModels(latch))
            val tt = task(func = compileTextures(latch))
            TaskManager.addTask(mt)
            TaskManager.addTask(tt)
            latch.await()
            Platform.runLater { compileState.set(true) }
            updateMessage("All compiled!")
        }

    }

    private fun compileModels(latch: CountDownLatch): TaskFunc = {
        val allModels = MinecraftData.filesFromJar.filterKeys { matchModel.matcher(it).find() }
        for ((n, mb) in allModels) {
            val input = ByteArrayInputStream(mb)
            val jr = Json.createReader(input)
            jr.runCatching { use {
                val o = readObject()
                val p = o.getString("parent")
                val t = o.getJsonObject("textures").mapNotNull { (k, v) ->
                    if (v is JsonString) k to v.string else null
                }.toMap()
                if (p != null) {
                    modelMap[n] = ModelData(p, t)
                }
            } }
        }
        latch.countDown()
    }

    private fun compileTextures(latch: CountDownLatch): TaskFunc = {
        val allTex = MinecraftData.filesFromJar.filterKeys { matchTex.matcher(it).find() }
        for ((n, tb) in allTex) {
            val input = ByteArrayInputStream(tb)
            val img = Image(input)
            textures[n] = img
        }
        latch.countDown()
    }

    fun getImageOf(name: String): Image? {
        if (!compileState.get()) return null
        val (ns, item) = decodeName(name)
        val m = "assets/$ns/models/item/$item.json"
        val model = modelMap[m]
        if (model != null) {
            if (model.textures.isEmpty()) {
                if (model.parent != null) {
                    return getImageOf(model.parent)
                }
                return null
            }
            val t = model.textures["layer0"]
            if (t != null) {
                return decodeNameToImage(t)
            }
            val ft = model.textures.values.first()
            return decodeNameToImage(ft)
        }
        return null
    }

    private fun decodeName(name: String): Pair<String, String> {
        if (name.indexOf(':') != -1) {
            val (ns, item) = name.split(":", limit = 2)
            return ns to item
        }
        return "minecraft" to name
    }

    private fun decodeNameToImage(name: String): Image? {
        if (!compileState.get()) return null
        val (ns, dir) = decodeName(name)
        val tf = "assets/$ns/textures/$dir.png"
        return textures[tf]
    }

    class ModelData(val parent: String?, val textures: Map<String, String>)
}