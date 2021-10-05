package szewek.craftery.mcdata

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import org.jetbrains.skia.Image
import szewek.craftery.net.Downloader
import szewek.craftery.util.ImageCache
import szewek.craftery.util.LongBiConsumer
import szewek.craftery.util.downloadJson

object ImageAlternatives {
    val mapIds = mapOf(
        "minecraft:item/chest" to 142901
    )
    private val images = mutableMapOf<String, Image>()

    fun getImage(name: String): Image? {
        if (name !in mapIds) return null
        if (name in images) return images[name]
        val fileId = mapIds[name]
        val obj = downloadJson<JsonNode>(
            Downloader.buildQuery(
                "https://minecraft.gamepedia.com/api.php",
                listOf(
                    "action" to "query",
                    "format" to "json",
                    "prop" to "imageinfo",
                    "iiprop" to "url",
                    "iiurlwidth" to 64,
                    "pageids" to fileId
                )
            ),
            LongBiConsumer.DUMMY
        )!!
        val iinfo: ArrayNode? = obj
            .path("query")
            .path("pages")
            .path(fileId.toString())
            .withArray("imageinfo")
        if (iinfo != null) {
            val url = iinfo.path(0)?.get("thumburl")?.asText() ?: return null
            val img = ImageCache.fromURL(url)
            images[name] = img
            return img
        }
        return null
    }
}