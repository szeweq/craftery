package szeweq.craftery.cfapi;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class CFAPI {
    private static final TypeReference<List<AddonSearch>> TREF_ADDON_SEARCH_LIST = new TypeReference<>() {};
    private static final TypeReference<List<AddonFile>> TREF_ADDON_FILE_LIST = new TypeReference<>() {};
    private static final TypeReference<LocalDateTime> TREF_TIMESTAMP = new TypeReference<>() {};

    public static List<AddonSearch> findAddons(String query, int type) {
        try {
            return CFAPIClient.getJson(TREF_ADDON_SEARCH_LIST, "addon/search", Map.ofEntries(
                    Map.entry("gameId", 432),
                    Map.entry("sectionId", type),
                    Map.entry("searchFilter", query)
            ));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<AddonFile> getAddonFiles(int addonId) {
        try {
            return CFAPIClient.getJson(TREF_ADDON_FILE_LIST, "addon/" + addonId + "/files", Map.of());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAddonChangelog(int addonId, int fileId) {
        try {
            return CFAPIClient.getString("addon/" + addonId + "/file/" + fileId + "/changelog", Map.of());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String downloadURL(int addonId, int fileId) {
        try {
            return CFAPIClient.getString("addon/" + addonId + "/file/" + fileId + "/download-url", Map.of());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime getTimestamp() {
        try {
            return CFAPIClient.getJson(TREF_TIMESTAMP, "addon/timestamp", Map.of());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CFAPI() {}
}
