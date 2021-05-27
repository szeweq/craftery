package szewek.craftery.cfapi;

import com.google.gson.reflect.TypeToken;
import kotlin.Pair;
import szewek.craftery.util.GsonBodyHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

final class CFAPIClient {
    private CFAPIClient() {}

    private static final HttpClient cli = HttpClient.newHttpClient();
    private static final URI CF_URI = URI.create("https://addons-ecs.forgesvc.net/api/v2/");

    private static URI canonizeURI(String path, List<Pair<String, Object>> params) {
        if (params.isEmpty()) {
            return CF_URI.resolve(path);
        } else {
            var sb = new StringBuilder();
            sb.append(path).append('?');
            var c = 0;
            for (var p : params) {
                if (c++ > 0) sb.append('&');
                sb.append(p.getFirst()).append('=').append(p.getSecond().toString());
            }
            return CF_URI.resolve(sb.toString());
        }
    }

    private static <T> HttpResponse<T> get(String path, List<Pair<String, Object>> params, final HttpResponse.BodyHandler<T> bodyHandler) throws IOException, InterruptedException {
        return cli.send(HttpRequest.newBuilder(canonizeURI(path, params)).build(), bodyHandler);
    }

    static <T> T getJson(TypeToken<T> token, String path, List<Pair<String, Object>> params) throws IOException, InterruptedException {
        return get(path, params, GsonBodyHandler.handle(token)).body();
    }

    static String getString(String path, List<Pair<String, Object>> params) throws IOException, InterruptedException {
        return get(path, params, HttpResponse.BodyHandlers.ofString()).body();
    }

}
