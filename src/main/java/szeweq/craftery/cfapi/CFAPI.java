package szeweq.craftery.cfapi;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class CFAPI {
    private static final TypeReference<List<AddonSearch>> TREF_ADDON_SEARCH_LIST = new TypeReference<>() {};
    private static final TypeReference<List<AddonFile>> TREF_ADDON_FILE_LIST = new TypeReference<>() {};
    private static final TypeReference<LocalDateTime> TREF_TIMESTAMP = new TypeReference<>() {};

    public static CompletableFuture<List<AddonSearch>> findAddons(String query, int type) {
            return CFAPIClient.getJson(TREF_ADDON_SEARCH_LIST, "addon/search", Map.ofEntries(
                    Map.entry("gameId", 432),
                    Map.entry("sectionId", type),
                    Map.entry("searchFilter", query)
            ));
    }

    public static CompletableFuture<List<AddonFile>> getAddonFiles(int addonId) {
        return CFAPIClient.getJson(TREF_ADDON_FILE_LIST, "addon/" + addonId + "/files", Map.of());
    }

    public static CompletableFuture<String> getAddonChangelog(int addonId, int fileId) {
        return CFAPIClient.getString("addon/" + addonId + "/file/" + fileId + "/changelog", Map.of());
    }

    public static CompletableFuture<String> downloadURL(int addonId, int fileId) {
        return CFAPIClient.getString("addon/" + addonId + "/file/" + fileId + "/download-url", Map.of());
    }

    public static CompletableFuture<LocalDateTime> getTimestamp() {
        return CFAPIClient.getJson(TREF_TIMESTAMP, "addon/timestamp", Map.of());
    }

    private CFAPI() {}
}
