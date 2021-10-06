package szeweq.craftery.net;

import com.fasterxml.jackson.core.type.TypeReference;
import kotlin.Pair;
import szeweq.craftery.util.LongBiConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

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
        return get(url, progress, HttpResponse.BodyHandlers.buffering(HttpResponse.BodyHandlers.ofInputStream(), 4096));
    }

    public static <T> T downloadJson(String url, LongBiConsumer progress) {
        return get(url, progress, JsonBodyHandler.handle());
    }

    public static <T> T downloadJson(String url, TypeReference<T> tref, LongBiConsumer progress) {
        return get(url, progress, JsonBodyHandler.handle(tref));
    }

    public static String buildQuery(String path, Collection<Pair<String, Object>> params) {
        if (params.isEmpty()) {
            return path;
        }
        var sb = new StringBuilder();
        sb.append(path).append('?');
        var c = 0;
        for (var p : params) {
            if (c++ > 0) sb.append('&');
            sb.append(p.getFirst()).append('=').append(p.getSecond().toString());
        }
        return sb.toString();
    }

    public static String buildQuery(String path, Map<String, Object> params) {
        if (params.isEmpty()) {
            return path;
        }
        var sb = new StringBuilder();
        sb.append(path).append('?');
        var c = 0;
        for (var p : params.entrySet()) {
            if (c++ > 0) sb.append('&');
            sb.append(p.getKey()).append('=').append(p.getValue().toString());
        }
        return sb.toString();
    }
}
