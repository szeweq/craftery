package szeweq.craftery.util;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import kotlin.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimeLogManager {
    private static final Map<String, ValueHistory> historyMap = new ConcurrentHashMap<>();

    public static final MutableState<Long> lastLog = SnapshotStateKt.mutableStateOf(System.nanoTime(), SnapshotStateKt.structuralEqualityPolicy());

    public static void logNano(String name, long nano) {
        var ll = System.nanoTime();
        var d = ll - nano;
        historyMap.computeIfAbsent(name, k -> new ValueHistory()).add(d);
        lastLog.setValue(ll);
    }

    public static String formatDuration(long d) {
        var ms = d / 1000_000L;
        var ns = d % 1000_000L;
        return ms + " ms " + ns + " ns";
    }

    public static List<Pair<String, Long>> averages() {
        return historyMap.entrySet()
                .stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue().avg()))
                .sorted((l, r) -> (int) (r.getSecond() - l.getSecond()))
                .toList();
    }

}
