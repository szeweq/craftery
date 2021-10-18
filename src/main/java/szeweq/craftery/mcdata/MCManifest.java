package szeweq.craftery.mcdata;

import java.util.List;
import java.util.Map;

public record MCManifest(Map<String, String> latest, List<MCVersion> versions) {
    public String getLatestRelease() {
        return latest.get("release");
    }

}
