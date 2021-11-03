package szeweq.craftery.util;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;

import java.util.Arrays;
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

    public static ComputedAverageEntry[] averages() {
        var result = new ComputedAverageEntry[historyMap.size()];
        var i = 0;
        for (var entry : historyMap.entrySet()) {
            result[i] = new ComputedAverageEntry(entry.getKey(), entry.getValue().avg());
            i++;
        }
        Arrays.sort(result);
        return result;
    }


    public record ComputedAverageEntry(String name, long avg) implements Comparable<ComputedAverageEntry> {
        @Override
        public int compareTo(ComputedAverageEntry o) {
            return Long.compare(avg, o.avg);
        }
    }
}
