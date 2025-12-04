package bob.cloverville.controllers;

import bob.cloverville.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MemberCreateController {

  @FXML private TextField txtName;
  @FXML private Spinner<Integer> spnPoints;
  @FXML private Spinner<Integer> spnTasksCompleted;

  @FXML private Button btnCreate;
  @FXML private Button btnClose;

  private final MemberService memberService = AppContext.get().memberService();

  @FXML
  public void initialize() {
    spnPoints.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 10));
    spnTasksCompleted.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
  }

  @FXML
  private void createMember() {
    try {
      String name = txtName.getText().trim();
      int points = spnPoints.getValue();
      int tasksCompleted = spnTasksCompleted.getValue();

      Member m = new Member(name, points, tasksCompleted);
      memberService.addMember(m);

      // Refresh dashboard members view
      AppContext.get().getDashboardController().loadMembersView();

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
