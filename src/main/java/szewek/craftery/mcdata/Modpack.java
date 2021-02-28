package szewek.craftery.mcdata;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kotlin.Pair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipInputStream;

public class Modpack {
    private Modpack() {}

    private static Gson GSON = new Gson();

    public static List<Pair<Integer, Integer>> readManifest(ZipInputStream input) {
        try {
            if (findManifest(input)) {
                var manifest = GSON.fromJson(new InputStreamReader(input), JsonObject.class);
                return StreamSupport.stream(manifest.getAsJsonArray("files").spliterator(), false)
                        .map(jv -> jv instanceof JsonObject ? jv.getAsJsonObject() : null)
                        .filter(Objects::nonNull)
                        .map(jo -> {
                            var pid = jo.getAsJsonPrimitive("projectID").getAsInt();
                            var fid = jo.getAsJsonPrimitive("fileID").getAsInt();
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
