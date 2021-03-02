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

    fun modelDataOf(name: String): ModelData? {
        val (ns, item) = decodeName(name)
        val m = "assets/$ns/models/$item.json"
        return modelMap[m]
    }

    fun getImageOf(name: String): Image? {
        if (!compileState) return null
        if (name == "") return null
        buildModelOf(name)
        val model = modelDataOf(name)
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

    fun buildModelOf(name: String): Model {
        if (!compileState) return Model.Failed
        if (name == "") return Model.Failed
        val model = modelDataOf(name)
        if (model != null) {
            val (_, path) = decodeName(name)
            if (path.startsWith("item")) {
                val t = model.textures["layer0"] ?: model.textures.values.first()
                return Model.Item(t)
            }
            val texRoutes = mutableMapOf<String, String>()
            val searched = mutableListOf<String>()
            collectTextures(name, model, texRoutes, searched)
            texRoutes.forEach { (k, v) -> println("$name - tex $k :: $v") }
            searched.reverse()
            val up = findTextureFor("up", texRoutes, searched)
            val north = findTextureFor("north", texRoutes, searched)
            val west = findTextureFor("west", texRoutes, searched)
            println("UP = $up; NORTH = $north; WEST = $west")
            return Model.Block(up, north, west)
        }
        return Model.Empty
    }

    private fun collectTextures(name: String, model: ModelData, texRoutes: MutableMap<String, String>, searched: MutableList<String>) {
        if (name in searched) return
        searched.add(name)
        for ((n, tx) in model.textures) {
            texRoutes["${name}#$n"] = if (tx.startsWith('#')) "@${model.parent}$tx" else tx
        }
        if (model.parent != null && model.parent != "") {
            val pmodel = modelDataOf(model.parent)
            if (pmodel != null) collectTextures(model.parent, pmodel, texRoutes, searched)
        }
    }

    private fun findTextureFor(sub: String, texRoutes: MutableMap<String, String>, searched: MutableList<String>): String {
        var current = sub
        for (s in searched) {
            val k = "$s#$current"
            println("Finding route $k...")
            val tr = texRoutes[k]
            if (tr != null) {
                if (!tr.startsWith('@')) return tr
                current = tr.substringAfterLast('#')
            }
        }
        return ""
    }

    fun lazyImageOf(name: String) = lazy { decodeNameToImage(name) }

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
