package szewek.craftery.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public final class RecordAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var cl = (Class<T>) type.getRawType();
        if (!cl.isRecord()) {
            return null;
        }
        var delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                } else {
                    var recordComponents = cl.getRecordComponents();
                    var typeMap = new HashMap<String, TypeToken<?>>();
                    for (int i = 0; i < recordComponents.length; i++) {
                        typeMap.put(recordComponents[i].getName(), TypeToken.get(recordComponents[i].getGenericType()));
                    }
                    var argsMap = new HashMap<String, Object>();
                    in.beginObject();
                    while (in.hasNext()) {
                        String name = in.nextName();
                        var token = typeMap.get(name);
                        if (token != null) {
                            argsMap.put(name, gson.getAdapter(token).read(in));
                        } else {
                            in.skipValue();
                        }
                    }
                    in.endObject();

                    var argTypes = new Class<?>[recordComponents.length];
                    var args = new Object[recordComponents.length];
                    for (int i = 0; i < recordComponents.length; i++) {
                        argTypes[i] = recordComponents[i].getType();
                        args[i] = argsMap.get(recordComponents[i].getName());
                    }
                    Constructor<T> constructor;
                    try {
                        constructor = cl.getDeclaredConstructor(argTypes);
                        constructor.setAccessible(true);
                        return constructor.newInstance(args);
                    } catch (NoSuchMethodException | InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }
}
