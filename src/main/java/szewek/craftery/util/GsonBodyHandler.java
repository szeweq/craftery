package szewek.craftery.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpResponse;

public final class GsonBodyHandler {
    private static final Gson GSON = new Gson();

    public static <T> HttpResponse.BodyHandler<T> handle(final TypeToken<T> token) {
        return ri -> HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofInputStream(), inputStream -> {
            try {
                return GSON.getAdapter(token).fromJson(new InputStreamReader(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
