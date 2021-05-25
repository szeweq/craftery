package szewek.craftery.util;

import androidx.compose.ui.graphics.DesktopImageAsset_desktopKt;
import androidx.compose.ui.graphics.ImageBitmap;
import kotlin.Pair;
import kotlin.Unit;
import org.jetbrains.skija.Image;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class ImageCache {
    private static final long refreshInterval = Duration.ofMinutes(5).toNanos();
    private static long lastRefresh = System.nanoTime();
    private static final Map<String, Image> map = new ConcurrentHashMap<>();
    private static final Map<String, ImageBitmap> mapBitmaps = new ConcurrentHashMap<>();
    private static final Queue<Pair<String, Consumer<ImageBitmap>>> imgQueue = new ConcurrentLinkedQueue<>();
    private static final AtomicBoolean downloading = new AtomicBoolean(false);

    public static void queue(String url, Consumer<ImageBitmap> cb) {
        imgQueue.add(new Pair<>(url, cb));
        if (!downloading.get()) {
            downloading.set(true);
            var t = new Thread(ImageCache::downloadImages);
            t.setDaemon(true);
            t.start();
        }
    }

    public static void lazyGet(String url, Consumer<ImageBitmap> cb) {
        if (mapBitmaps.containsKey(url)) {
            cb.accept(mapBitmaps.get(url));
        } else {
            queue(url, cb);
        }
    }

    public static void downloadImages() {
        while (imgQueue.peek() != null) {
            var p = imgQueue.poll();
            var s = p.getFirst();
            var stream = Downloader.INSTANCE.downloadFile(s, (l1, l2) -> Unit.INSTANCE);
            Image img = null;
            try {
                img = Image.makeFromEncoded(stream.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            var ib = img == null ? null : DesktopImageAsset_desktopKt.asImageBitmap(img);
            if (ib != null)
                mapBitmaps.put(s, ib);
            p.getSecond().accept(ib);
        }
        downloading.set(false);
    }

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

    public static void recycle() {
        long now = System.nanoTime();
        if (now - lastRefresh >= refreshInterval) {
            map.clear();
            mapBitmaps.clear();
            lastRefresh = System.nanoTime();
        }
    }
}
