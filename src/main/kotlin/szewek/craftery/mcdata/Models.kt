package szewek.craftery.mcdata

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.skija.Image
import java.io.ByteArrayInputStream
import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern


object Models {
    private val GSON = Gson()
    private val modelMap = mutableMapOf<String, ModelData>()
    private val textures = mutableMapOf<String, Image>()
    var compileState by mutableStateOf(false)
        private set
    private val matchModel = Pattern.compile("^assets/[a-z]+/models/.*\\.json$")
    private val matchTex = Pattern.compile("^assets/[a-z]+/textures/.*\\.png$")

    fun compile() {
        println("Compiling resources")
        val latch = CountDownLatch(2)
        with (GlobalScope) {
            launch { compileTextures(latch) }
            launch { compileModels(latch) }
        }
        latch.await()
        compileState = true
        println("All compiled!")
    }

    private fun compileModels(latch: CountDownLatch) {
        val allModels = MinecraftData.filesFromJar.filterKeys { matchModel.matcher(it).find() }
        for ((n, mb) in allModels) {
            val input = ByteArrayInputStream(mb)
            val jr = GSON.newJsonReader(input.reader())
            jr.runCatching { use {
                val o = GSON.fromJson(this, JsonObject::class.java) as JsonObject
                val p = o["parent"].asString
                val t = if (o.has("textures")) o.getAsJsonObject("textures").entrySet().mapNotNull { (k, v) ->
                    val s = checkTextures(v)
                    if (s != null) k to s else null
                }.toMap() else emptyMap()
                if (p != null) {
                    modelMap[n] = ModelData(p, t)
                }
            } }
        }
        latch.countDown()
    }

    private fun checkTextures(v: JsonElement): String? {
        if (v.isJsonPrimitive) {
            val pr = v.asJsonPrimitive
            if (pr.isString)
                return pr.asString
        }
        return null
    }

    private fun compileTextures(latch: CountDownLatch) {
        val allTex = MinecraftData.filesFromJar.filterKeys { matchTex.matcher(it).find() }
        for ((n, tb) in allTex) {
            val input = ByteArrayInputStream(tb)
            val img = Image.makeFromEncoded(input.readAllBytes())
            textures[n] = img // scale(img, 2)
        }
        latch.countDown()
    }

    /* private fun scale(img: Image, scale: Int): Image {
        val w = img.width.toInt()
        val h = img.height.toInt()
        if (w == 0 && h == 0 || scale == 1) {
            return img
        }
        val wimg = WritableImage(w * scale, h * scale)
        val pr = img.pixelReader
        val pw = wimg.pixelWriter
        for (y in 0 until h) {
            for (x in 0 until w) {
                val argb = pr.getArgb(x, y)
                for (dx in 0 until scale) {
                    for (dy in 0 until scale) {
                        pw.setArgb(scale * x + dx, scale * y + dy, argb)
                    }
                }
            }
        }
        return wimg
    } */

    fun getImageOf(name: String): Image? {
        if (!compileState) return null
        if (name == "") return null
        val (ns, item) = decodeName(name)
        val m = "assets/$ns/models/$item.json"
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
        if (!compileState) return null
        val (ns, dir) = decodeName(name)
        val tf = "assets/$ns/textures/$dir.png"
        return textures[tf]
    }

    class ModelData(val parent: String?, val textures: Map<String, String>)
}
