package szeweq.craftery.util;

import androidx.compose.ui.graphics.*;
import org.jetbrains.skia.Image;
import szeweq.craftery.net.Downloader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class ImageCache {
    private static final Executor exec = Executors.newWorkStealingPool();
    private static final HttpClient cli = HttpClient.newBuilder().executor(exec).build();
    private static final long refreshInterval = Duration.ofMinutes(5).toNanos();
    private static long lastRefresh = System.nanoTime();
    private static final Map<String, Image> map = new ConcurrentHashMap<>();
    private static final Map<String, ImageBitmap> mapBitmaps = new ConcurrentHashMap<>();
    public static final ImageBitmap emptyBitmap = UtilsKt.getEmptyImage();

    public static void lazyGet(String url, Consumer<ImageBitmap> cb) {
        if (mapBitmaps.containsKey(url)) {
            cb.accept(mapBitmaps.get(url));
        } else {
            downloadImage(url, cb);
        }
    }

    public static void downloadImage(final String url, Consumer<ImageBitmap> cb) {
        final var hr = HttpRequest.newBuilder(URI.create(url)).build();
        cli.sendAsync(hr, ImageCache::bodyHandlerOfImage)
                .thenApply(HttpResponse::body)
                .thenApply(img -> {
                    final var ib = img == null ? null : DesktopImageAsset_desktopKt.toComposeImageBitmap(img);
                    if (ib != null) {
                        mapBitmaps.put(url, ib);
                        return ib;
                    }
                    return emptyBitmap;
                }).thenAccept(cb);
    }

    private static HttpResponse.BodySubscriber<Image> bodyHandlerOfImage(HttpResponse.ResponseInfo ri) {
        return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(), Image.Companion::makeFromEncoded);
    }

    public static Image fromURL(String url) {
        recycle();
        return map.computeIfAbsent(url, s -> {
            var stream = Downloader.downloadFile(s, LongBiConsumer.DUMMY);
            Image img = null;
            try {
                byte[] b = stream.readAllBytes();
                stream.close();
                img = Image.Companion.makeFromEncoded(b);
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
