package bob.cloverville.controllers;

import bob.cloverville.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UserAccountController {

  @FXML private Label lblUsername;
  @FXML private Label lblEmail; // optional if you have an email field
  @FXML private PasswordField txtPassword;
  @FXML private Label lblPasswordError;
  @FXML private Button btnSave;
  @FXML private Button btnClose;

  private final UserAccService userService = AppContext.get().userAccService();
  private UserAccount currentUser;

  public void setUser(UserAccount user) {
    this.currentUser = user;
    populateFields();
  }

  private void populateFields() {
    lblUsername.setText(currentUser.getUsername());
    txtPassword.setText(""); // always empty by default
    lblPasswordError.setText("");
  }

  @FXML
  private void savePassword() {
    try {
      String newPassword = txtPassword.getText().trim();
      if (newPassword.isBlank()) {
        lblPasswordError.setText("Password cannot be empty.");
        return;
      }
      userService.changePassword(currentUser, newPassword);
      lblPasswordError.setText("Password updated successfully!");
      txtPassword.clear();
    } catch (IllegalArgumentException ex) {
      lblPasswordError.setText(ex.getMessage());
    } catch (Exception ex) {
      lblPasswordError.setText("An unexpected error occurred.");
      ex.printStackTrace();
    }
  }

  @FXML
  private void closeWindow() {
    Stage stage = (Stage) btnClose.getScene().getWindow();
    stage.close();
  }
}
