package szewek.craftery.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import szewek.craftery.util.RecordAdapterFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpResponse;

public final class GsonBodyHandler {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapterFactory(new RecordAdapterFactory()).create();

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
