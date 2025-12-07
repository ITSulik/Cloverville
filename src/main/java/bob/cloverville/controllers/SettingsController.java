package bob.cloverville.controllers;

import bob.cloverville.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class SettingsController {

  @FXML private TextField txtCommunityPoints;
  @FXML private TextField txtCommunityGoal;
  @FXML private TextField txtTargetPoints;
  @FXML private Label lblLastReset;
  @FXML private Label lblPointReset;

  @FXML private Button btnSave;
  @FXML private Button btnClose;
  @FXML private Button btnResetPoints;
  @FXML private Button btnReset;

  private final SettingsService settingsService = AppContext.get().settingsService();
  private final MemberService memberService = AppContext.get().memberService();
  private final ActivityService activityService = AppContext.get().activityService();

  private Settings settings;

  @FXML
  public void initialize() {
    settings = settingsService.getSettings();
    loadSettings();
  }
  @FXML
  private void resetCommunalTasksManually() {
    // Reset COMMUNAL tasks
    activityService.getAll().stream()
        .filter(a -> a.getType() == ActivityType.COMMUNAL)
        .forEach(a -> {
          a.setDeadline(LocalDate.now().plusWeeks(1));
          a.setCreatedAt(LocalDate.now());
          a.setCompletedAt(null);
          a.setPerformerID(null);
          a.setReceiverID(null);
          activityService.updateActivity(a);
        });

    // Update last reset date
    settingsService.updateLastResetDate();
    memberService.applyWeeklyBonusAndReset();

    // Optional: Feedback
    Alert alert = new Alert(Alert.AlertType.INFORMATION,
        "COMMUNAL tasks have been reset successfully!", ButtonType.OK);
    alert.showAndWait();

    // Refresh Dashboard table if open
    DashboardController dashboard = AppContext.get().getDashboardController();
    if (dashboard != null && "TASKS".equals(dashboard.getCurrentMode())) {
      dashboard.loadTasksView();
    }

  }

  @FXML
  private void resetPointsManually() {
    // Reset community points
    memberService.resetAllPoints();
    settings.setPointResetDate(LocalDate.now());
    settingsService.save();

    // Optional: Feedback
    Alert alert = new Alert(Alert.AlertType.INFORMATION,
        "Community points have been reset successfully!", ButtonType.OK);
    alert.showAndWait();

    // Reload settings display
    loadSettings();
  }

  private void loadSettings() {
    txtCommunityPoints.setText(String.valueOf(settings.getCommunityPoints()));
    txtCommunityGoal.setText(settings.getCommunityGoal());
    txtTargetPoints.setText(String.valueOf(settings.getTargetPoints()));
    lblPointReset.setText(settings.getPointResetDate().toString());
    lblLastReset.setText(settings.getLastResetDate().toString());
  }

  @FXML
  private void saveSettings() {
    try {
      int points = Integer.parseInt(txtCommunityPoints.getText().trim());
      String goal = txtCommunityGoal.getText().trim();
      int target = Integer.parseInt(txtTargetPoints.getText().trim());

      if (goal.isBlank())
        throw new IllegalArgumentException("Community goal cannot be blank.");

      settings.setCommunityPoints(points);
      settings.setCommunityGoal(goal);
      settings.setTargetPoints(target);

      settingsService.save();

      Alert alert = new Alert(Alert.AlertType.INFORMATION, "Settings saved successfully.", ButtonType.OK);
      alert.showAndWait();

    } catch (NumberFormatException ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Points and Target must be valid numbers.", ButtonType.OK);
      alert.showAndWait();
    } catch (IllegalArgumentException ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
      alert.showAndWait();
    }
  }

  @FXML
  private void closeWindow() {
    Stage stage = (Stage) btnClose.getScene().getWindow();
    stage.close();
  }
}
