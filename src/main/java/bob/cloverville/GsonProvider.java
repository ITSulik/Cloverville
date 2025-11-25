package bob.cloverville;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonProvider {

  private static final Gson gson = new GsonBuilder()
      .setPrettyPrinting()
      .serializeNulls()
      .create();

  public static Gson get() {
    return gson;
  }
}

