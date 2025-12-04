package bob.cloverville;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class UIUtils {

  public static void setInlineError(Label label, String message) {
    if (message == null || message.isBlank()) {
      label.setVisible(false);
      label.setText("");
      label.setStyle("");
      return;
    }

    label.setVisible(true);
    label.setText(message);
  }
  public static void switchScene(Stage stage, String fxmlPath, String title) {
    try {
      FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource(fxmlPath));
      Parent root = loader.load();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle(title);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
