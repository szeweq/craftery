package szewek.mctool.util;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class KtUtil {

	@NotNull
	public static <T> Stream<T> streamValuesFrom(Map<?, T> map) {
		return map.values().stream();
	}

	@NotNull
	public static <K, V> Map<K, V> buildMap(Consumer<Map<K, V>> c) {
		if (c == null) return Map.of();
		Map<K, V> map = new LinkedHashMap<>();
		c.accept(map);
		//noinspection unchecked
		return Map.ofEntries(map.entrySet().toArray(new Map.Entry[0]));
	}

	private KtUtil() {}
}
