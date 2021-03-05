package szewek.craftery.util;

import kotlin.Unit;
import org.jetbrains.skija.Image;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageCache {
    private static final long refreshInterval = Duration.ofMinutes(5).toNanos();
    private static long lastRefresh = System.nanoTime();
    private static final Map<String, Image> map = new ConcurrentHashMap<>();

    public static Image fromURL(String url) {
        recycle();
        return map.computeIfAbsent(url, s -> {
            var stream = Downloader.INSTANCE.downloadFile(url, (l1, l2) -> Unit.INSTANCE);
            Image img = null;
            try {
                img = Image.makeFromEncoded(stream.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return img;
        });
    }

    public static void recycle() {
        long now = System.nanoTime();
        if (now - lastRefresh >= refreshInterval) {
            map.clear();
            lastRefresh = System.nanoTime();
        }
    }
}
