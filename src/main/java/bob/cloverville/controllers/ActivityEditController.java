package bob.cloverville.controllers;

import bob.cloverville.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class ActivityEditController {

  @FXML private Label lblId;
  @FXML private Label lblCreatedAt;

  @FXML private TextField txtTitle;
  @FXML private TextArea txtDescription;
  @FXML private Spinner<Integer> spnPoints;

  @FXML private DatePicker dpDeadline;

  @FXML private Button btnSave;
  @FXML private Button btnComplete;
  @FXML private Button btnCancel;

  @FXML private ComboBox<Member> cbPerformer;
  @FXML private ComboBox<Member> cbReceiver;

  private Activity activity;

  private final MemberService memberService = AppContext.get().memberService();
  private final ActivityService activityService = AppContext.get().activityService();

  @FXML
  public void initialize() {
    // --- Points spinner ---
    spnPoints.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0)
    );

    // --- Member dropdowns ---
    var members = memberService.getAll();

    cbPerformer.getItems().setAll(members);
    cbReceiver.getItems().setAll(members);

    // Display member name in dropdown
    StringConverter<Member> converter = new StringConverter<>() {
      @Override
      public String toString(Member m) {
        return (m == null ? "" : m.getName());
      }

      @Override
      public Member fromString(String s) {
        return null; // not needed
      }
    };

    cbPerformer.setConverter(converter);
    cbReceiver.setConverter(converter);
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
    populateFields();
    configureFieldsByType();
  }

  private void populateFields() {
    lblId.setText(activity.getId().toString());
    lblCreatedAt.setText(activity.getCreatedAt().toString());

    txtTitle.setText(activity.getTitle());
    txtDescription.setText(activity.getDescription());
    spnPoints.getValueFactory().setValue(activity.getPointValue());

    dpDeadline.setValue(activity.getDeadline());

    // Select performer
    if (activity.getPerformerID() != null) {
      cbPerformer.getSelectionModel().select(
          cbPerformer.getItems().stream()
              .filter(m -> m.getId().equals(activity.getPerformerID()))
              .findFirst().orElse(null)
      );
    }

    // Select receiver
    if (activity.getReceiverID() != null) {
      cbReceiver.getSelectionModel().select(
          cbReceiver.getItems().stream()
              .filter(m -> m.getId().equals(activity.getReceiverID()))
              .findFirst().orElse(null)
      );
    }
  }

  private void configureFieldsByType() {
    ActivityType type = activity.getType();

    switch (type) {
      case GREEN:
        dpDeadline.setDisable(true);
        cbPerformer.setDisable(true);
        cbReceiver.setDisable(true);
        btnComplete.setDisable(true);
        break;

      case COMMUNAL:
        dpDeadline.setDisable(false);
        cbPerformer.setDisable(false); // performer will be assigned later
        cbReceiver.setDisable(true);
        btnComplete.setDisable(activity.getCompletedAt() != null);
        break;

      case TRADE_TASK:
      case TRADE_GOODS:
        dpDeadline.setDisable(false);
        cbPerformer.setDisable(true); // cannot change performer
        cbReceiver.setDisable(false);
        btnComplete.setDisable(false);
        break;
    }
  }

  @FXML
  private void saveChanges() {
    try {
      // Validate title/description
      String title = txtTitle.getText().trim();
      String desc = txtDescription.getText().trim();
      int points = spnPoints.getValue();

      activity.setTitle(title);
      activity.setDescription(desc);
      activity.setPointValue(points);

      // Validate deadline
      if (!dpDeadline.isDisabled()) {
        activity.setDeadline(dpDeadline.getValue());
      }

      // Validate performer/receiver
      if (!cbPerformer.isDisabled()) {
        Member performer = cbPerformer.getValue();
        activity.setPerformerID(performer != null ? performer.getId() : null);
      }

      if (!cbReceiver.isDisabled()) {
        Member receiver = cbReceiver.getValue();
        activity.setReceiverID(receiver != null ? receiver.getId() : null);
      }

      activityService.updateActivity(activity);
      AppContext.get().getDashboardController().loadTasksView();
      ((Stage) btnSave.getScene().getWindow()).close();

    } catch (Exception ex) {
      Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
      a.showAndWait();
    }
  }

  @FXML
  private void completeActivity() {
    try {
      saveChanges();
      activityService.completeActivity(activity.getId());
      AppContext.get().getDashboardController().loadTasksView();
      closeWindow();
    } catch (Exception ex) {
      Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
      a.showAndWait();
    }
  }

  @FXML
  private void closeWindow() {
    Stage stage = (Stage) btnCancel.getScene().getWindow();
    stage.close();
  }
}
