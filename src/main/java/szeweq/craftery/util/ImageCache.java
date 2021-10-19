package szeweq.craftery.util;

import androidx.compose.ui.graphics.*;
import org.jetbrains.skia.Image;
import szeweq.craftery.net.Downloader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
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
            downloadImage(url)
                    .thenApply(SkiaImageAsset_skikoKt::toComposeImageBitmap)
                    .whenComplete((imageBitmap, th) -> {
                        if (th == null && imageBitmap != null) {
                            mapBitmaps.put(url, imageBitmap);
                        }
                    })
                    .thenAccept(cb);
        }
    }

    private static HttpResponse.BodySubscriber<Image> bodyHandlerOfImage(HttpResponse.ResponseInfo ri) {
        return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(), Image.Companion::makeFromEncoded);
    }

    private static CompletableFuture<Image> downloadImage(final String url) {
        final var hr = HttpRequest.newBuilder(URI.create(url)).build();
        return cli.sendAsync(hr, ImageCache::bodyHandlerOfImage).thenApply(HttpResponse::body);
    }

    public static Image fromURL(String url) {
        recycle();
        return map.computeIfAbsent(url, s -> {
            Image img = null;
            try {
                img = downloadImage(s).get();
            } catch (InterruptedException | ExecutionException e) {
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
