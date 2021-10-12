package szeweq.craftery.cfapi;

import com.fasterxml.jackson.core.type.TypeReference;
import szeweq.craftery.net.Downloader;
import szeweq.craftery.net.JsonBodyHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

final class CFAPIClient {
    private CFAPIClient() {}

    private static final HttpClient cli = HttpClient.newHttpClient();
    private static final URI CF_URI = URI.create("https://addons-ecs.forgesvc.net/api/v2/");

    private static URI canonizeURI(String path, Map<String, Object> params) {
        return CF_URI.resolve(Downloader.buildQuery(path, params));
    }

    private static <T> CompletableFuture<HttpResponse<T>> get(String path, Map<String, Object> params, final HttpResponse.BodyHandler<T> bodyHandler) {
        return cli.sendAsync(HttpRequest.newBuilder(canonizeURI(path, params)).build(), bodyHandler);
    }

    static <T> CompletableFuture<T> getJson(TypeReference<T> tref, String path, Map<String, Object> params) {
        return get(path, params, JsonBodyHandler.handle(tref)).thenApplyAsync(HttpResponse::body);
    }

    static CompletableFuture<String> getString(String path, Map<String, Object> params) {
        return get(path, params, HttpResponse.BodyHandlers.ofString()).thenApplyAsync(HttpResponse::body);
    }

}
