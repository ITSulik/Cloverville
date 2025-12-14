package bob.cloverville;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;

public class GsonProvider {

  private static final Gson gson = new GsonBuilder()

      // --- LocalDate support ---
      .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
          return LocalDate.parse(json.getAsString());
        }
      })
      .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext ctx) {
          return new JsonPrimitive(src.toString());
        }
      })

      // --- Enum support (optional but recommended) ---
      .registerTypeAdapter(ActivityType.class, new JsonDeserializer<ActivityType>() {
        @Override
        public ActivityType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
          return ActivityType.valueOf(json.getAsString());
        }
      })
      .registerTypeAdapter(ActivityType.class, new JsonSerializer<ActivityType>() {
        @Override
        public JsonElement serialize(ActivityType src, Type typeOfSrc, JsonSerializationContext ctx) {
          return new JsonPrimitive(src.name());
        }
      })

      .setPrettyPrinting()
      .serializeNulls()
      .create();

  public static Gson get() {
    return gson;
  }
}
