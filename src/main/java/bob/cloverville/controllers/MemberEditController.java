package bob.cloverville.controllers;

import bob.cloverville.Member;
import bob.cloverville.AppContext;
import bob.cloverville.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MemberEditController {

  @FXML private TextField txtName;
  @FXML private TextField txtPoints;
  @FXML private TextField txtTasksCompleted;

  @FXML private Button btnSave;
  @FXML private Button btnCancel;

  private Member member;
  private final MemberService memberService = AppContext.get().memberService();

  public void setMember(Member m) {
    this.member = m;
    txtName.setText(m.getName());
    txtPoints.setText(String.valueOf(m.getPersonalPoints()));
    txtTasksCompleted.setText(String.valueOf(m.getTotalTasksCompleted()));
  }

  @FXML
  public void initialize() {
    btnCancel.setOnAction(e -> ((Stage) btnCancel.getScene().getWindow()).close());

    btnSave.setOnAction(e -> {
      try {
        member.setName(txtName.getText().trim());
        member.setPoints(Integer.parseInt(txtPoints.getText().trim()));
        member.setTotalTasksCompleted(Integer.parseInt(txtTasksCompleted.getText().trim()));

        memberService.updateMember(member);
        ((Stage) btnSave.getScene().getWindow()).close();
      } catch (Exception ex) {
        ex.printStackTrace();
        // optionally show an alert for validation errors
      }
    });
  }
}
