package szeweq.craftery.mcdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.skia.Image;
import szeweq.craftery.net.Downloader;
import szeweq.craftery.util.ImageCache;
import szeweq.craftery.util.LongBiConsumer;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ImageAlternatives {
    static Map<String, Integer> mapIds = Map.of(
            "minecraft:item/chest", 142901
    );
    private static final Map<String, Image> images = new LinkedHashMap<>();
    private static final TypeReference<JsonNode> jsonNodeTypeReference = new TypeReference<>() {};

    @Nullable
    public static Image getImage(String name) {
        if (!mapIds.containsKey(name)) return null;
        if (images.containsKey(name)) return images.get(name);
        final var fileId = mapIds.get(name);
        Map<String, Object> query = Map.of(
                "action", "query",
                "format", "json",
                "prop", "imageinfo",
                "iiprop", "url",
                "iiurlwidth", 64,
                "pageids", fileId
        );
        JsonNode obj = Downloader.downloadJson(
                Downloader.buildQuery("https://minecraft.gamepedia.com/api.php", query),
                jsonNodeTypeReference,
                LongBiConsumer.DUMMY
        );
        ArrayNode iinfo = obj.path("query").path("pages").path(fileId.toString()).withArray("imageinfo");
        if (iinfo != null) {
            var url = iinfo.path(0).get("thumburl").asText();
            var img = ImageCache.fromURL(url);
            images.put(name, img);
            return img;
        }
        return null;
    }

    private ImageAlternatives() {}
}
