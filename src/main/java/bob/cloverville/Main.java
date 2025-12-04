package bob.cloverville;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage stage) throws Exception {

    // Ensure AppContext singleton is initialized (first call will initialize services)
    AppContext.get();

    // Load login view
    FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
    Scene scene = new Scene(loader.load());
    stage.setScene(scene);
    stage.setTitle("Cloverville Login");
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
