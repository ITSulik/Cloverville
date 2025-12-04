package bob.cloverville.controllers;

import bob.cloverville.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class LoginController {

  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private Label errorLabel;

  private final UserAccService userAccService = AppContext.get().userAccService();

  @FXML
  public void initialize() {
    UIUtils.setInlineError(errorLabel, null);
  }

  @FXML
  public void onLogin() {

    UIUtils.setInlineError(errorLabel, null);

    try {
      String username = usernameField.getText();
      String password = passwordField.getText();

      if (username == null || username.isBlank())
        throw new IllegalArgumentException("Please enter your username.");
      if (password == null || password.isBlank())
        throw new IllegalArgumentException("Please enter your password.");

      UserAccount acc = userAccService.authenticate(username, password);

      if (acc == null) {
        UIUtils.setInlineError(errorLabel, "Invalid username or password.");
        return;
      }

      // SUCCESS: switch scene
      Stage stage = (Stage) usernameField.getScene().getWindow();
      UIUtils.switchScene(stage, "/bob/cloverville/dashboard.fxml", "Cloverville — Dashboard");

    } catch (IllegalArgumentException ex) {
      UIUtils.setInlineError(errorLabel, ex.getMessage());
    } catch (Exception ex) {
      UIUtils.setInlineError(errorLabel, "Login failed: " + ex.getMessage());
    }
  }


  @FXML
  public void onQuit() {
    Stage stage = (Stage) usernameField.getScene().getWindow();
    stage.close();
  }
}
