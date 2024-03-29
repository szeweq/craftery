package szeweq.kt;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Various utility methods for making Kotlin code development easier
 */
public class KtUtil {

	@NotNull
	public static <T> Stream<T> streamValuesFrom(Map<?, T> map) {
		return map.values().stream();
	}

	public static <K, V> Stream<Pair<K, V>> streamEntriesFrom(Map<K, V> map) {
		return map.entrySet().stream().map(KtUtil::entriesToPairs);
	}

	public static <T> Stream<T> streamInstances(final Stream<?> stream, final Class<T> cl) {
		return stream.mapMulti((t, c) -> {
			if (cl.isInstance(t)) c.accept(cl.cast(t));
		});
	}

	public static <K, V> Pair<K, V> entriesToPairs(Map.Entry<K, V> entry) {
		return new Pair<>(entry.getKey(), entry.getValue());
	}

	public static <K, V> Collector<Pair<K, V>, ?, Map<K, V>> pairsToMap() {
		return Collectors.toMap(Pair::getFirst, Pair::getSecond);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static <K, V> Map<K, V> buildMap(Consumer<Map<K, V>> c) {
		if (c == null) return Map.of();
		Map<K, V> map = new LinkedHashMap<>();
		c.accept(map);
		return Map.ofEntries(map.entrySet().toArray(new Map.Entry[0]));
	}

	private static final String SIZES = "kMGT";
	private static final String sizeFormat = "%.2f %cB";

	public static String lengthInBytes(long size) {
		var i = 0;
		double x = size;
		while (x >= 1024.0) {
			x /= 1024.0;
			i++;
		}
		if (i == 0) {
			return size + " bytes";
		}
		if (i > 4) { i = 4; }
		return sizeFormat.formatted(x, SIZES.charAt(i - 1));
	}

	private KtUtil() {}
}
