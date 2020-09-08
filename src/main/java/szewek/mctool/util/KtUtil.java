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

	@SuppressWarnings("unchecked")
	@NotNull
	public static <K, V> Map<K, V> buildMap(Consumer<Map<K, V>> c) {
		if (c == null) return Map.of();
		Map<K, V> map = new LinkedHashMap<>();
		c.accept(map);
		return Map.ofEntries(map.entrySet().toArray(new Map.Entry[0]));
	}

	private static final String SIZES = "kMGT";

	public static String lengthInBytes(long size) {
		var i = 0;
		double x = size;
		while (x >= 1024.0) {
			x /= 1024.0;
			i++;
		}
		if (i == 0) {
			return "" + size + " bytes";
		}
		if (i > 4) { i = 4; }
		return String.format("%.2f %cB", x, SIZES.charAt(i - 1));
	}

	private KtUtil() {}
}
