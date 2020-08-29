package szewek.mctool.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class KtUtil {

	public static <T> Stream<T> streamValuesFrom(Map<?, T> map) {
		return map.values().stream();
	}

	public static <K, V> Map<K, V> buildMap(Consumer<Map<K, V>> c) {
		Map<K, V> map = new LinkedHashMap<>();
		c.accept(map);
		//noinspection unchecked
		return Map.ofEntries(map.entrySet().toArray(new Map.Entry[0]));
	}

	private KtUtil() {}
}
