package szeweq.craftery.mcdata;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kotlin.Pair;
import szeweq.craftery.util.JsonUtil;
import szeweq.craftery.util.JsonUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipInputStream;

public class Modpack {
    private Modpack() {}

    public static List<Pair<Integer, Integer>> readManifest(ZipInputStream input) {
        try {
            if (findManifest(input)) {
                var mtree = JsonUtil.mapper.readTree(input);
                ArrayNode files = mtree.withArray("files");
                return StreamSupport.stream(files.spliterator(), false)
                        .map(jv -> jv instanceof ObjectNode ? (ObjectNode) jv : null)
                        .filter(Objects::nonNull)
                        .map(jo -> {
                            final var pid = jo.get("projectID").asInt();
                            final var fid = jo.get("fileID").asInt();
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
