package szewek.craftery.util;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class Downloader {
    private Downloader() {}
    private static final HttpClient cli = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    private static <T> T get(String url, LongBiConsumer progress, HttpResponse.BodyHandler<T> bodyHandler) {
        try {
            return cli.send(HttpRequest.newBuilder(URI.create(url)).build(), ProgressSubscriber.handle(bodyHandler, progress)).body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream downloadFile(String url, LongBiConsumer progress) {
        return get(url, progress, HttpResponse.BodyHandlers.ofInputStream());
    }

    public static <T> T downloadJson(String url, LongBiConsumer progress) {
        return get(url, progress, GsonBodyHandler.handle(new TypeToken<T>() {}));
    }
}
