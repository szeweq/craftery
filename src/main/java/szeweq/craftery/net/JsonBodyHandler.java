package szeweq.craftery.net;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import szeweq.craftery.util.JsonUtil;
import szeweq.craftery.util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;

public class JsonBodyHandler {
    public static <T> HttpResponse.BodyHandler<T> handle() {
        return handle(new TypeReference<>() {});
    }

    public static <T>HttpResponse.BodyHandler<T> handle(final Class<T> cl) {
        return handle(new TypeReference<T>() {
            @Override
            public Type getType() {
                return cl;
            }
        });
    }

    public static <T> HttpResponse.BodyHandler<T> handle(final TypeReference<T> tref) {
        final var or = JsonUtil.mapper.readerFor(tref);
        return ri -> HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofInputStream(), inputStream -> {
            try {
                return or.readValue(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
