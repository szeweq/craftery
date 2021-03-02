package szewek.craftery.mcdata

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.jetbrains.skija.Image
import szewek.craftery.util.ImageCache

object ImageAlternatives {
    val mapIds = mapOf(
        "minecraft:item/chest" to 142901
    )
    private val images = mutableMapOf<String, Image>()

    fun getImage(name: String): Image? {
        if (name !in mapIds) return null
        if (name in images) return images[name]
        val fileId = mapIds[name]
        val obj = Fuel.get(
            "https://minecraft.gamepedia.com/api.php",
            listOf(
                "action" to "query",
                "format" to "json",
                "prop" to "imageinfo",
                "iiprop" to "url",
                "iiurlwidth" to 64,
                "pageids" to fileId
            )
        ).responseObject<JsonObject>().third.get()
        val iinfo = traverse(obj, "query", "pages", fileId.toString(), "imageinfo")?.asJsonArray
        if (iinfo != null) {
            val url = iinfo[0]?.asJsonObject?.get("thumburl")?.asString ?: return null
            val img = ImageCache.fromURL(url)
            images[name] = img
            return img
        }
        return null
    }

    private fun traverse(o: JsonObject, vararg paths: String): JsonElement? {
        val names = paths.dropLast(1)
        val last = paths.last()
        var current = o
        for (n in names) {
            current = current[n].asJsonObject
        }
        return current[last]
    }
}