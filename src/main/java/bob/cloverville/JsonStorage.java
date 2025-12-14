package bob.cloverville;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage<T> {

  private final String filePath;
  private final Gson gson;
  private final Type listType;

  public JsonStorage(String filePath, Type listType) {
    this.filePath = filePath;
    this.listType = listType;
    this.gson = GsonProvider.get();
  }

  // -------- LIST STORAGE --------
  public List<T> load() {
    try (FileReader reader = new FileReader(filePath, StandardCharsets.UTF_8)) {
      List<T> data = gson.fromJson(reader, listType);
      return data != null ? data : new ArrayList<>();
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  public void save(List<T> data) {
    try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
      gson.toJson(data, writer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // -------- SINGLE VALUE STORAGE --------
  public T loadSingle() {
    try (FileReader reader = new FileReader(filePath, StandardCharsets.UTF_8)) {
      return gson.fromJson(reader, (Type) ((Class<?>) listType));
    } catch (Exception e) {
      return null;
    }
  }


  public void saveSingle(T value) {
    try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
      gson.toJson(value, writer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
