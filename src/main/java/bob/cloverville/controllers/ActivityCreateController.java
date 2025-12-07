package bob.cloverville.controllers;

import bob.cloverville.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class ActivityCreateController {

  @FXML private ComboBox<ActivityType> cbType;
  @FXML private TextField txtTitle;
  @FXML private TextArea txtDescription;
  @FXML private Spinner<Integer> spnPoints;
  @FXML private DatePicker dpDeadline;
  @FXML private ComboBox<Member> cbPerformer;
  @FXML private ComboBox<Member> cbReceiver;

  @FXML private Button btnCreate;
  @FXML private Button btnClose;

  private final MemberService memberService = AppContext.get().memberService();
  private final ActivityService activityService = AppContext.get().activityService();

  @FXML
  public void initialize() {
    // Populate type dropdown
    cbType.getItems().setAll(ActivityType.values());

    // Points spinner
    spnPoints.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));

    // Populate members dropdowns
    List<Member> members = memberService.getAll();
    cbPerformer.getItems().setAll(members);
    cbReceiver.getItems().setAll(members);

    StringConverter<Member> memberConverter = new StringConverter<>() {
      @Override
      public String toString(Member m) {
        return (m == null) ? "" : m.getName();
      }

      @Override
      public Member fromString(String s) { return null; }
    };
    cbPerformer.setConverter(memberConverter);
    cbReceiver.setConverter(memberConverter);

    // Listen for type changes
    cbType.setOnAction(e -> configureFieldsByType());
  }

  private void configureFieldsByType() {
    ActivityType type = cbType.getValue();
    if (type == null) return;

    // Reset optional fields first
    dpDeadline.setValue(null);
    cbPerformer.getSelectionModel().clearSelection();
    cbReceiver.getSelectionModel().clearSelection();

    switch (type) {
      case GREEN:
        dpDeadline.setDisable(true);
        cbPerformer.setDisable(true);
        cbReceiver.setDisable(true);
        break;

      case COMMUNAL:
        dpDeadline.setDisable(false);
        cbPerformer.setDisable(true);
        cbReceiver.setDisable(true);
        dpDeadline.setValue(LocalDate.now().plusDays(7)); // default deadline
        break;

      case TRADE_TASK:
      case TRADE_GOODS:
        dpDeadline.setDisable(false);
        cbPerformer.setDisable(false);
        cbReceiver.setDisable(false);
        break;
    }
  }

  @FXML
  private void createActivity() {
    try {
      ActivityType type = cbType.getValue();
      if (type == null) throw new IllegalArgumentException("Select an activity type.");

      String title = txtTitle.getText().trim();
      String desc = txtDescription.getText().trim();
      int points = spnPoints.getValue();

      Member performer = cbPerformer.getValue();
      Member receiver = cbReceiver.getValue();
      LocalDate deadline = dpDeadline.getValue();

      // Create activity according to rules
      Activity a = new Activity(
          title,
          desc,
          points,
          (type == ActivityType.GREEN || type == ActivityType.COMMUNAL) ? null : (performer != null ? performer.getId() : null),
          (type == ActivityType.GREEN || type == ActivityType.COMMUNAL) ? null : (receiver != null ? receiver.getId() : null),
          type,
          deadline
      );

      activityService.addActivity(a);
      if (cbReceiver.getValue() != null && cbPerformer.getValue() != null){
        activityService.completeActivity(a.getId());
      }

      // Refresh dashboard
      AppContext.get().getDashboardController().loadTasksView();

      closeWindow();
    } catch (Exception ex) {
      Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
      a.showAndWait();
    }
  }

  @FXML
  private void closeWindow() {
    Stage stage = (Stage) btnClose.getScene().getWindow();
    stage.close();
  }
}
