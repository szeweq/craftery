package szewek.craftery.mcdata

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image
import szewek.craftery.util.JsonUtil
import java.io.ByteArrayInputStream
import java.util.regex.Pattern


object Models {
    //private val GSON = Gson()
    private val modelDataMap = mutableMapOf<String, ModelData>()
    private val modelsMap = mutableMapOf<String, Model>()
    private val textures = mutableMapOf<String, Image>()
    var compileState by mutableStateOf(false)
        private set
    private val matchModel = Pattern.compile("^assets/[a-z]+/models/.*\\.json$")
    private val matchTex = Pattern.compile("^assets/[a-z]+/textures/.*\\.png$")

    suspend fun compile() = coroutineScope {
        println("Compiling resources")
        val texTask = launch { compileTextures() }
        val modelTask = launch { compileModels() }
        texTask.join()
        modelTask.join()
        compileState = true
        println("All compiled!")
    }

    private fun compileModels() {
        val allModels = MinecraftData.filesFromJar.filterKeys { matchModel.matcher(it).find() }
        for ((n, mb) in allModels) {
            val input = ByteArrayInputStream(mb)
            val jr = JsonUtil.mapper.readTree(input)
            //val jr = GSON.newJsonReader(input.reader())
            jr.runCatching {
                val p = get("parent").asText()
                val t = if (has("textures")) (with("textures") as ObjectNode)
                    .fields().asSequence()
                    .mapNotNull { (k, v) ->
                        val s = checkTextures(v)
                        if (s != null) k to s else null
                    }
                    .toMap() else emptyMap()
                if (p != null) {
                    modelDataMap[n] = ModelData(p, t)
                }
            }
        }
    }

    private fun checkTextures(v: JsonNode): String? {
        return v.asText()
    }

    private fun compileTextures() {
        val allTex = MinecraftData.filesFromJar.filterKeys { matchTex.matcher(it).find() }
        for ((n, tb) in allTex) {
            val input = ByteArrayInputStream(tb)
            val img = Image.makeFromEncoded(input.readAllBytes())
            textures[n] = img // scale(img, 2)
        }
    }

    fun modelDataOf(name: String): ModelData? {
        val (ns, item) = decodeName(name)
        val m = "assets/$ns/models/$item.json"
        return modelDataMap[m]
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

    fun getModelOf(name: String): Model {
        if (!compileState) return Model.Empty
        if (name == "") return Model.Failed
        if (name in modelsMap) return modelsMap[name]!!
        val m = buildModelOf(name)
        if (m != null) {
            modelsMap[name] = m
            return m
        }
        return Model.Failed
    }

    fun buildModelOf(name: String): Model? {
        val model = modelDataOf(name)
        if (model != null) {
            val (_, path) = decodeName(name)
            if (name in ImageAlternatives.mapIds) {
                val img = ImageAlternatives.getImage(name)
                return if (img != null) Model.Custom(img) else null
            }
            if (path.startsWith("item") && model.textures.isNotEmpty()) {
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
