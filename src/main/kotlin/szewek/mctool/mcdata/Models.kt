package szewek.mctool.mcdata

import javafx.scene.image.Image
import java.io.ByteArrayInputStream
import javax.json.Json
import javax.json.JsonString

object Models {
    val modelMap = mutableMapOf<String, ModelData>()
    val textures = mutableMapOf<String, Image>()

    fun compileModels() {
        val allModels = MinecraftData.filesFromJar.filterKeys { it.matches(Regex.fromLiteral("^assets/[a-z]+/models")) }
        for ((n, mb) in allModels) {
            val input = ByteArrayInputStream(mb)
            val o = Json.createParser(input).getObject()
            val p = o.getString("parent")
            val t = o.getJsonObject("textures").mapNotNull { (k, v) ->
                if (v is JsonString) k to v.string else null
            }.toMap()
            if (p != null) {
                modelMap[n] = ModelData(p, t)
            }
        }
    }

    fun compileTextures() {
        val allTex = MinecraftData.filesFromJar.filterKeys { it.matches(Regex.fromLiteral("^assets/[a-z]+/textures")) }
        for ((n, tb) in allTex) {
            val input = ByteArrayInputStream(tb)
            val img = Image(input)
            textures[n] = img
        }
    }

    class ModelData(val parent: String?, val textures: Map<String, String>)
}