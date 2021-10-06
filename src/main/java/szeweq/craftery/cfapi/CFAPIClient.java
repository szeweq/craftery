package szeweq.craftery.cfapi;

import com.fasterxml.jackson.core.type.TypeReference;
import kotlin.Pair;
import szeweq.craftery.net.Downloader;
import szeweq.craftery.net.JsonBodyHandler;
import szeweq.craftery.net.Downloader;
import szeweq.craftery.net.JsonBodyHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class CFAPIClient {
    private CFAPIClient() {}

    private static final HttpClient cli = HttpClient.newHttpClient();
    private static final URI CF_URI = URI.create("https://addons-ecs.forgesvc.net/api/v2/");

    private static URI canonizeURI(String path, Map<String, Object> params) {
        return CF_URI.resolve(Downloader.buildQuery(path, params));
    }

    private static <T> HttpResponse<T> get(String path, Map<String, Object> params, final HttpResponse.BodyHandler<T> bodyHandler) throws IOException, InterruptedException {
        return cli.send(HttpRequest.newBuilder(canonizeURI(path, params)).build(), bodyHandler);
    }

    static <T> T getJson(TypeReference<T> tref, String path, Map<String, Object> params) throws IOException, InterruptedException {
        return get(path, params, JsonBodyHandler.handle(tref)).body();
    }

    static String getString(String path, Map<String, Object> params) throws IOException, InterruptedException {
        return get(path, params, HttpResponse.BodyHandlers.ofString()).body();
    }

}
