package szewek.craftery.mcdata;

import kotlin.Pair;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

public class Modpack {
    private Modpack() {}

    public static List<Pair<Integer, Integer>> readManifest(ZipInputStream input) {
        try {
            if (findManifest(input)) {
                var jr = Json.createReader(input);
                var manifest = jr.readObject();
                return manifest.getJsonArray("files").stream()
                        .map(jv -> jv instanceof JsonObject ? jv.asJsonObject() : null)
                        .filter(Objects::nonNull)
                        .map(jo -> {
                            var pid = jo.getInt("projectID");
                            var fid = jo.getInt("fileID");
                            return new Pair<>(pid, fid);
                        })
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static boolean findManifest(ZipInputStream input) throws IOException {
        do {
            var e = input.getNextEntry();
            if (e == null) {
                return false;
            }
            if (e.getName().endsWith("manifest.json")) {
                return true;
            }
        } while (true);
    }
}
