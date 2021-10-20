package szeweq.craftery.mcdata;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kotlin.Pair;
import szeweq.craftery.util.JsonUtil;
import szeweq.craftery.util.KtUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import java.util.zip.ZipInputStream;

public class Modpack {
    private Modpack() {}

    public static List<IntPair> readManifest(ZipInputStream input) {
        try {
            if (findManifest(input)) {
                var mtree = JsonUtil.mapper.readTree(input);
                ArrayNode files = mtree.withArray("files");
                var spl = Spliterators.spliterator(files.elements(), files.size(), Spliterator.ORDERED);

                return KtUtil.streamInstances(StreamSupport.stream(spl, false), ObjectNode.class)
                        .map(jo -> {
                            final var pid = jo.get("projectID").asInt();
                            final var fid = jo.get("fileID").asInt();
                            return new Pair<>(pid, fid);
                        })
                        .toList();
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
