package bob.cloverville.controllers;

import bob.cloverville.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DashboardController {

  @FXML private Button btnMembers;
  @FXML private Button btnTasks;
  @FXML private Button btnAdd;

  @FXML private TextField searchField;
  @FXML private Button filterButton;

  @FXML private TableView tableView;
  @FXML private VBox mainContent;

  private ViewMode currentMode = ViewMode.TASKS;

  private enum ViewMode { MEMBERS, TASKS }

  private final MemberService memberService = AppContext.get().memberService();
  private final ActivityService activityService = AppContext.get().activityService();

  private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @FXML
  public void initialize() {
    setupButtons();
    loadCurrentView();
    setupSearch();
    setupAddButton();
    AppContext.get().setDashboardController(this);


    tableView.setRowFactory(tv -> {
      TableRow<Object> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (!row.isEmpty() && e.getClickCount() == 2) {
          Object rowData = row.getItem();
          openViewWindow(rowData);
        }
      });
      return row;
    });

  }

  private void setupAddButton() {
    btnAdd.setOnAction(e -> {
      if (currentMode == ViewMode.MEMBERS) {
        openCreateMember();
      } else {
        openCreateActivity();
      }
    });
  }

  @FXML
  private void openCreateMember() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/bob/cloverville/memberCreate.fxml"));
      Parent root = loader.load();
      Stage stage = new Stage();
      stage.setTitle("Create Member");
      stage.setScene(new Scene(root));
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void openCreateActivity() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/bob/cloverville/activityCreate.fxml"));
      Parent root = loader.load();

      Stage stage = new Stage();
      stage.setTitle("Create New Activity");
      stage.setScene(new Scene(root));
      stage.initModality(Modality.APPLICATION_MODAL); // blocks interaction with the dashboard until closed
      stage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Create Activity window.", ButtonType.OK);
      alert.showAndWait();
    }
  }

  // ---------------------------------------------------------
  // BUTTON BEHAVIOR
  // ---------------------------------------------------------
  private void setupButtons() {
    btnMembers.setOnAction(e -> switchView(ViewMode.MEMBERS));
    btnTasks.setOnAction(e -> switchView(ViewMode.TASKS));
    highlightSelectedButton();
  }

  private void switchView(ViewMode mode) {
    currentMode = mode;
    highlightSelectedButton();
    loadCurrentView();
  }

  private void highlightSelectedButton() {
    btnMembers.setStyle("");
    btnTasks.setStyle("");

    if (currentMode == ViewMode.MEMBERS) btnMembers.setStyle("-fx-background-color: #cfcfcf;");
    else btnTasks.setStyle("-fx-background-color: #cfcfcf;");
  }

  // ---------------------------------------------------------
  // MAIN VIEW LOADING
  // ---------------------------------------------------------
  private void loadCurrentView() {
    if (currentMode == ViewMode.MEMBERS) loadMembersView();
    else loadTasksView();
  }

  public void loadMembersView() {
    tableView.getColumns().clear();

    TableColumn<Member, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));

    TableColumn<Member, Number> ppCol = new TableColumn<>("Personal Points");
    ppCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getPersonalPoints()));

    TableColumn<Member, Number> tcCol = new TableColumn<>("Tasks Completed");
    tcCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getTotalTasksCompleted()));

    tableView.getColumns().addAll(nameCol, ppCol, tcCol);
    tableView.getColumns().add(buildActionsColumn());

    tableView.setItems(FXCollections.observableArrayList(memberService.getAll()));
  }

  public void loadTasksView() {
    tableView.getColumns().clear();

    TableColumn<Activity, String> titleCol = new TableColumn<>("Title");
    titleCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));

    TableColumn<Activity, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType().name()));

    TableColumn<Activity, Number> pointsCol = new TableColumn<>("Points");
    pointsCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getPointValue()));

    TableColumn<Activity, String> deadlineCol = new TableColumn<>("Deadline");
    deadlineCol.setCellValueFactory(cell -> {
      LocalDate d = cell.getValue().getDeadline();
      return new SimpleStringProperty(d != null ? d.toString() : "");
    });

    TableColumn<Activity, String> performerCol = new TableColumn<>("Performer");
    performerCol.setCellValueFactory(cell -> {
      UUID pid = cell.getValue().getPerformerID();
      Member m = pid != null ? memberService.getById(pid) : null;
      return new SimpleStringProperty(m != null ? m.getName() : "");
    });

    TableColumn<Activity, String> receiverCol = new TableColumn<>("Receiver");
    receiverCol.setCellValueFactory(cell -> {
      UUID rid = cell.getValue().getReceiverID();
      Member m = rid != null ? memberService.getById(rid) : null;
      return new SimpleStringProperty(m != null ? m.getName() : "");
    });

    tableView.getColumns().addAll(titleCol, typeCol, pointsCol, deadlineCol, performerCol, receiverCol);
    tableView.getColumns().add(buildActionsColumn());

    tableView.setItems(FXCollections.observableArrayList(activityService.getAll()));
  }


  // ---------------------------------------------------------
  // SEARCH BAR
  // ---------------------------------------------------------
  private void setupSearch() {
    searchField.textProperty().addListener((obs, oldVal, newVal) -> applySearch(newVal));
  }

  private void applySearch(String query) {
    if (currentMode == ViewMode.MEMBERS) {
      List<Member> filtered = memberService.getAll().stream()
          .filter(m -> m.getName().toLowerCase().contains(query.toLowerCase()))
          .collect(Collectors.toList());
      tableView.setItems(FXCollections.observableArrayList(filtered));
    } else {
      List<Activity> filtered = activityService.getAll().stream()
          .filter(a -> a.getTitle().toLowerCase().contains(query.toLowerCase()))
          .collect(Collectors.toList());
      tableView.setItems(FXCollections.observableArrayList(filtered));
    }
  }

  private <T> TableColumn<T, Void> buildActionsColumn() {
    TableColumn<T, Void> col = new TableColumn<>("Actions");

    col.setCellFactory(param -> new TableCell<>() {

      private final Button btnView = new Button("👁");
      private final Button btnEdit = new Button("✏");
      private final Button btnDelete = new Button("🗑");

      {
        btnView.setOnAction(e -> {
          T item = getTableView().getItems().get(getIndex());
          openViewWindow(item);
        });

        btnEdit.setOnAction(e -> {
          T item = getTableView().getItems().get(getIndex());
          openEditWindow(item);
        });

        btnDelete.setOnAction(e -> {
          T item = getTableView().getItems().get(getIndex());
          deleteItem(item);
        });

        btnView.setStyle("-fx-font-size: 14px;");
        btnEdit.setStyle("-fx-font-size: 14px;");
        btnDelete.setStyle("-fx-font-size: 14px;");
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          ToolBar bar = new ToolBar(btnView, btnEdit, btnDelete);
          bar.setStyle("-fx-background-color: transparent;");
          setGraphic(bar);
        }
      }
    });

    col.setPrefWidth(140);
    return col;
  }

  public void openViewWindow(Object item) {
    try {
      if (item instanceof Member m) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bob/cloverville/memberView.fxml"));
        Stage s = new Stage();
        s.setScene(new Scene(loader.load()));
        MemberViewController c = loader.getController();
        c.setMember(m);
        s.show();
      }
      else if (item instanceof Activity a) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bob/cloverville/activityView.fxml"));
        Stage s = new Stage();
        s.setScene(new Scene(loader.load()));
        ActivityViewController c = loader.getController();
        c.setActivity(a);
        s.show();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  public void openEditWindow(Object item) {
    try {
      if (item instanceof Member m) {
        FXMLLoader loader = new FXMLLoader(DashboardController.class.getResource("/bob/cloverville/memberEdit.fxml"));
        Stage s = new Stage();
        s.setScene(new Scene(loader.load()));
        MemberEditController c = loader.getController();
        c.setMember(m);
        s.show();
      }
      else if (item instanceof Activity a) {
        FXMLLoader loader = new FXMLLoader(DashboardController.class.getResource("/bob/cloverville/activityEdit.fxml"));
        Stage s = new Stage();
        s.setScene(new Scene(loader.load()));
        ActivityEditController c = loader.getController();
        c.setActivity(a);
        s.show();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  private void deleteItem(Object item) {
    System.out.println("DELETE: " + item);

    if (item instanceof Member m) {
      memberService.deleteMember(m);
      loadMembersView();
    }
    if (item instanceof Activity a) {
      activityService.deleteActivity(a);
      loadTasksView();
    }
  }


}
