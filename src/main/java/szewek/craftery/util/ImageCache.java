package szewek.craftery.util;

import androidx.compose.ui.graphics.DesktopImageAsset_desktopKt;
import androidx.compose.ui.graphics.ImageBitmap;
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
    private static final Map<String, ImageBitmap> mapBitmaps = new ConcurrentHashMap<>();

    public static Image fromURL(String url) {
        recycle();
        return map.computeIfAbsent(url, s -> {
            var stream = Downloader.INSTANCE.downloadFile(s, (l1, l2) -> Unit.INSTANCE);
            Image img = null;
            try {
                img = Image.makeFromEncoded(stream.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return img;
        });
    }

    public static ImageBitmap bitmapFromURL(String url) {
        return mapBitmaps.computeIfAbsent(url, s -> {
            var img = fromURL(s);
            if (img == null) return null;
            else return DesktopImageAsset_desktopKt.asImageBitmap(img);
        });
    }

    public static void recycle() {
        long now = System.nanoTime();
        if (now - lastRefresh >= refreshInterval) {
            map.clear();
            mapBitmaps.clear();
            lastRefresh = System.nanoTime();
        }
    }
}
