package bob.cloverville.controllers;

import bob.cloverville.Member;
import bob.cloverville.AppContext;
import bob.cloverville.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MemberViewController {

  @FXML private Label lblName;
  @FXML private Label lblPoints;
  @FXML private Label lblTasksCompleted;

  @FXML private Button btnEdit;
  @FXML private Button btnDelete;
  @FXML private Button btnClose;

  private Member member;
  private final MemberService memberService = AppContext.get().memberService();

  public void setMember(Member m) {
    this.member = m;
    populateFields();
  }

  private void populateFields() {
    lblName.setText(member.getName());
    lblPoints.setText(String.valueOf(member.getPersonalPoints()));
    lblTasksCompleted.setText(String.valueOf(member.getTotalTasksCompleted()));
  }

  @FXML
  public void initialize() {
    btnClose.setOnAction(e -> ((Stage) btnClose.getScene().getWindow()).close());

    btnEdit.setOnAction(e -> {
      try {
        AppContext.get().getDashboardController().openEditWindow(member); // optional: reuse method
        ((Stage) btnEdit.getScene().getWindow()).close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    btnDelete.setOnAction(e -> {
      memberService.deleteMember(member);
      ((Stage) btnDelete.getScene().getWindow()).close();
    });
  }
}
