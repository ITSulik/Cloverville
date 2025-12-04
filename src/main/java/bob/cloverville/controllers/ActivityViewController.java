package bob.cloverville.controllers;

import bob.cloverville.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ActivityViewController {

  @FXML private Label lblId;
  @FXML private Label lblType;
  @FXML private Label lblTitle;
  @FXML private Label lblDescription;
  @FXML private Label lblPoints;
  @FXML private Label lblCreatedAt;
  @FXML private Label lblDeadline;
  @FXML private Label lblPerformer;
  @FXML private Label lblReceiver;
  @FXML private Label lblCompletedAt;

  @FXML private Button btnEdit;
  @FXML private Button btnDelete;
  @FXML private Button btnClose;

  private Activity activity;
  private final ActivityService activityService = AppContext.get().activityService();

  public void setActivity(Activity activity) {
    this.activity = activity;
    populateFields();
  }

  private void populateFields() {
    lblId.setText(activity.getId().toString());
    lblType.setText(activity.getType().name());
    lblTitle.setText(activity.getTitle());
    lblDescription.setText(activity.getDescription());
    lblPoints.setText(String.valueOf(activity.getPointValue()));
    lblCreatedAt.setText(activity.getCreatedAt().toString());
    lblDeadline.setText(activity.getDeadline() != null ? activity.getDeadline().toString() : "-");
    lblPerformer.setText(activity.getPerformerID() != null ? activity.getPerformerID().toString() : "-");
    lblReceiver.setText(activity.getReceiverID() != null ? activity.getReceiverID().toString() : "-");
    lblCompletedAt.setText(activity.getCompletedAt() != null ? activity.getCompletedAt().toString() : "-");
  }

  @FXML
  private void closeWindow() {
    Stage stage = (Stage) btnClose.getScene().getWindow();
    stage.close();
  }
  @FXML
  private void editActivity() {
    try {
      AppContext.get().getDashboardController().openEditWindow(activity);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    closeWindow();
  }
  @FXML
  private void deleteActivity() {
    try {
      activityService.deleteActivity(activity);
      AppContext.get().getDashboardController().loadTasksView();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    closeWindow();
  }


  // Optional: expose public handlers so Dashboard can call
  public Button getBtnEdit() { return btnEdit; }
  public Button getBtnDelete() { return btnDelete; }
}
