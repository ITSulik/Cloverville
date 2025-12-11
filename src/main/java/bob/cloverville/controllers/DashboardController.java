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
  @FXML private Button btnUser;

  @FXML private TextField searchField;
  @FXML private Button filterButton;
  @FXML private ComboBox<String> filterComboBox;


  @FXML private TableView<Object> tableView;
  @FXML private VBox mainContent;

  private ViewMode currentMode = ViewMode.TASKS;

  private String lastFilter = "Default";


  private enum ViewMode { MEMBERS, TASKS }

  private final MemberService memberService = AppContext.get().memberService();
  private final ActivityService activityService = AppContext.get().activityService();

  private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public String getCurrentMode() {
    return currentMode.name();
  }
  @FXML
  public void initialize() {
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    activityService.weeklyGreenReset();
    setupButtons();
    resetWeeklyCommunalTasks();
    resetPersonalPointsIfDue();
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
      });return row;
    });
    filterComboBox.getItems().clear();
    filterComboBox.getItems().addAll(
        "Default",
        "Completed",
        "With Deadline",
        "GREEN Activities",
        "COMMUNAL Activities",
        "TRADE Tasks",
        "TRADE Goods",
        "All"
    );

    filterComboBox.setValue(lastFilter); // initial value
    filterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal == null) {
        // don't call applyFilter with null; restore lastFilter if needed
        filterComboBox.setValue(lastFilter);
        return;
      }
      lastFilter = newVal;
      // only apply filter when we're actually on tasks view
      if (currentMode == ViewMode.TASKS) {
        applyFilter(newVal);
      }
    });

  }

  private void applyFilter(String filter) {
    List<Activity> allActivities = activityService.getAll();
    List<Activity> filtered;

    if (filter == null) filter = lastFilter != null ? lastFilter : "Default";
    lastFilter = filter;

    switch (filter) {
      case "Completed":
        filtered = allActivities.stream()
            .filter(a -> a.getCompletedAt() != null)
            .toList();
        break;

      case "With Deadline":
        filtered = allActivities.stream()
            .filter(a -> a.getDeadline() != null)
            .toList();
        break;

      case "GREEN Activities":
        filtered = allActivities.stream()
            .filter(a -> a.getType() == ActivityType.GREEN)
            .toList();
        break;

      case "COMMUNAL Activities":
        filtered = allActivities.stream()
            .filter(a -> a.getType() == ActivityType.COMMUNAL)
            .toList();
        break;

      case "TRADE Tasks":
        filtered = allActivities.stream()
            .filter(a -> a.getType() == ActivityType.TRADE_TASK)
            .toList();
        break;

      case "TRADE Goods":
        filtered = allActivities.stream()
            .filter(a -> a.getType() == ActivityType.TRADE_GOODS)
            .toList();
        break;

      case "All":
        filtered = allActivities;
        break;

      default: // "Default"
        filtered = allActivities.stream()
            .filter(a -> a.getCompletedAt() == null && a.getType() != ActivityType.GREEN)
            .toList();
        break;
    }

    tableView.setItems(FXCollections.observableArrayList(filtered));
  }

  private void resetWeeklyCommunalTasks() {
    if (!AppContext.get().settingsService().isWeeklyResetDue())
      return;

    activityService.getAll().stream()
        .filter(a -> a.getType() == ActivityType.COMMUNAL)
        .forEach(a -> {
          a.setDeadline(LocalDate.now().plusWeeks(1));
          a.setCreatedAt(LocalDate.now());
          a.setPerformerID(null);
          activityService.updateActivity(a);
        });
    memberService.applyWeeklyBonusAndReset();

    AppContext.get().settingsService().updateLastResetDate();
  }

  private void resetPersonalPointsIfDue() {
    SettingsService settingsService = AppContext.get().settingsService();
    if (settingsService.isMonthlyPointResetDue()) {
      memberService.resetAllPoints();
      settingsService.updatePointResetDate();
    }
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
  private void hideFilterControls() {
    filterComboBox.setVisible(false);
    filterComboBox.setManaged(false);
  }
  private void showFilterControls() {
    filterComboBox.setVisible(true);
    filterComboBox.setManaged(true);

    if (filterComboBox.getValue() == null) {
      filterComboBox.setValue(lastFilter != null ? lastFilter : "Default");
    } else {
      // re-apply current value to ensure listener runs/visual matches internal
      String v = filterComboBox.getValue();
      filterComboBox.setValue(v);
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
    btnMembers.setStyle("-fx-background-color: #ffffff;  -fx-background-radius: 8;  -fx-border-radius: 8;  -fx-border-color: #8fc48a;");
    btnTasks.setStyle("-fx-background-color: #ffffff;  -fx-background-radius: 8;  -fx-border-radius: 8;  -fx-border-color: #8fc48a;");

    if (currentMode == ViewMode.MEMBERS) btnMembers.setStyle("-fx-background-color: #cfcfcf; -fx-background-radius: 8;  -fx-border-radius: 8;  -fx-border-color: #8fc48a;");
    else btnTasks.setStyle("-fx-background-color: #cfcfcf; -fx-background-radius: 8;  -fx-border-radius: 8;  -fx-border-color: #8fc48a;");
  }

  // ---------------------------------------------------------
  // MAIN VIEW LOADING
  // ---------------------------------------------------------
  private void loadCurrentView() {
    if (currentMode == ViewMode.MEMBERS) loadMembersView();
    else loadTasksView();
  }

  public void loadMembersView() {
    hideFilterControls();
    tableView.getColumns().clear();

    TableColumn<Object, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(cell -> {
      Member m = (Member) cell.getValue();
      return new SimpleStringProperty(m.getName());
    });

    TableColumn<Object, Number> ppCol = new TableColumn<>("Personal Points");
    ppCol.setCellValueFactory(cell -> {
      Member m = (Member) cell.getValue();
      return new SimpleIntegerProperty(m.getPersonalPoints());
    });

    TableColumn<Object, Number> tcCol = new TableColumn<>("Tasks Completed");
    tcCol.setCellValueFactory(cell -> {
      Member m = (Member) cell.getValue();
      return new SimpleIntegerProperty(m.getTotalTasksCompleted());
    });

    tableView.getColumns().addAll(nameCol, ppCol, tcCol,buildActionsColumn());

    tableView.setItems(FXCollections.observableArrayList(memberService.getAll()));
  }

  public void loadTasksView() {
    showFilterControls();

    tableView.getColumns().clear();

    TableColumn<Object, String> titleCol = new TableColumn<>("Title");
    titleCol.setCellValueFactory(cell -> {
      Activity a = (Activity) cell.getValue();
      return new SimpleStringProperty(a.getTitle());
    });

    TableColumn<Object, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(cell -> {
      Activity a = (Activity) cell.getValue();
      return new SimpleStringProperty(a.getType().name());
    });

    TableColumn<Object, Number> pointsCol = new TableColumn<>("Points");
    pointsCol.setCellValueFactory(cell -> {
      Activity a = (Activity) cell.getValue();
      return new SimpleIntegerProperty(a.getPointValue());
    });

    TableColumn<Object, String> deadlineCol = new TableColumn<>("Deadline");
    deadlineCol.setCellValueFactory(cell -> {
      Activity a = (Activity) cell.getValue();
      LocalDate d = a.getDeadline();
      return new SimpleStringProperty(d != null ? d.toString() : "");
    });


    // ---------- PERFORMER ----------
    TableColumn<Object, String> performerCol = new TableColumn<>("Performer");
    performerCol.setCellValueFactory(cell -> {
      Activity a = (Activity) cell.getValue();
      UUID pid = a.getPerformerID();
      Member m = pid != null ? memberService.getById(pid) : null;
      return new SimpleStringProperty(m != null ? m.getName() : "");
    });

    // ---------- RECEIVER ----------
    TableColumn<Object, String> receiverCol = new TableColumn<>("Receiver");
    receiverCol.setCellValueFactory(cell -> {
      Activity a = (Activity) cell.getValue();
      UUID rid = a.getReceiverID();
      Member m = rid != null ? memberService.getById(rid) : null;
      return new SimpleStringProperty(m != null ? m.getName() : "");
    });

    tableView.getColumns().addAll(titleCol, typeCol, pointsCol, deadlineCol, performerCol, receiverCol, buildActionsColumn());

    // ---------- APPLY LAST USED FILTER ----------
    String filter = filterComboBox.getValue();
    if (filter == null) filter = lastFilter != null ? lastFilter : "Default";

    applyFilter(filter);
  }




  // ---------------------------------------------------------
  // SEARCH BAR
  // ---------------------------------------------------------
  private void setupSearch() {
    searchField.textProperty().addListener((obs, oldVal, newVal) -> applySearch(newVal));
  }

  private void applySearch(String query) {
    if (currentMode == ViewMode.MEMBERS) {
      List<Member> allMembers = memberService.getAll();
      if (query.isBlank()) {
        tableView.setItems(FXCollections.observableArrayList(allMembers));
        return;
      }

      tableView.setItems(
          FXCollections.observableArrayList(
              allMembers.stream()
                  .filter(m -> m.getName().toLowerCase().contains(query.toLowerCase()))
                  .toList()
          )
      );
      return;
    }

    // TASKS SEARCH + FILTER COMBINATION
    List<Activity> filteredActivities;

    switch (lastFilter != null ? lastFilter : "Default") {
      case "Completed":
        filteredActivities = activityService.getAll().stream()
            .filter(a -> a.getCompletedAt() != null)
            .toList();
        break;
      case "With Deadline":
        filteredActivities = activityService.getAll().stream()
            .filter(a -> a.getDeadline() != null)
            .toList();
        break;
      case "GREEN Activities":
        filteredActivities = activityService.getAll().stream()
            .filter(a -> a.getType() == ActivityType.GREEN)
            .toList();
        break;
      case "COMMUNAL Activities":
        filteredActivities = activityService.getAll().stream()
            .filter(a -> a.getType() == ActivityType.COMMUNAL)
            .toList();
        break;
      case "TRADE Tasks":
        filteredActivities = activityService.getAll().stream()
            .filter(a -> a.getType() == ActivityType.TRADE_TASK)
            .toList();
        break;
      case "TRADE Goods":
        filteredActivities = activityService.getAll().stream()
            .filter(a -> a.getType() == ActivityType.TRADE_GOODS)
            .toList();
        break;
      case "All":
        filteredActivities = activityService.getAll();
        break;
      default: // "Default"
        filteredActivities = activityService.getAll().stream()
            .filter(a -> a.getCompletedAt() == null && a.getType() != ActivityType.GREEN)
            .toList();
        break;
    }

    if (!query.isBlank()) {
      filteredActivities = filteredActivities.stream()
          .filter(a -> a.getTitle().toLowerCase().contains(query.toLowerCase()))
          .toList();
    }

    tableView.setItems(FXCollections.observableArrayList(filteredActivities));
  }




  private TableColumn<Object, Void> buildActionsColumn() {
    TableColumn<Object, Void> col = new TableColumn<>("Actions");

    col.setCellFactory(param -> new TableCell<>() {

      private final Button btnView = new Button("👁");
      private final Button btnEdit = new Button("✏");
      private final Button btnDelete = new Button("🗑");

      {
        btnView.setOnAction(e -> {
          Object item = getTableView().getItems().get(getIndex());
          openViewWindow(item);
        });

        btnEdit.setOnAction(e -> {
          Object item = getTableView().getItems().get(getIndex());
          openEditWindow(item);
        });

        btnDelete.setOnAction(e -> {
          Object item = getTableView().getItems().get(getIndex());

          Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
          confirm.setTitle("Confirm Delete");
          confirm.setHeaderText(null);
          confirm.setContentText("Are you sure you want to delete this item?");

          // Wait for user response
          confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
              deleteItem(item);
            }
          });
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

  @FXML
  private void openUserAccount() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/bob/cloverville/userAcc.fxml"));
      Stage stage = new Stage();
      stage.setTitle("User Account");
      stage.setScene(new Scene(loader.load()));
      stage.initModality(Modality.APPLICATION_MODAL);

      // Pass the current logged-in user
      UserAccountController controller = loader.getController();
      controller.setUser(AppContext.get().getCurrentUser()); // make sure you have currentUser in AppContext

      stage.showAndWait();
    } catch (Exception e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open User Account window.", ButtonType.OK);
      alert.showAndWait();
    }
  }

  @FXML
  private void openSettingsWindow() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/bob/cloverville/settings.fxml"));
      Stage stage = new Stage();
      stage.setTitle("Application Settings");
      stage.setScene(new Scene(loader.load()));
      stage.initModality(Modality.APPLICATION_MODAL); // blocks dashboard until closed
      stage.showAndWait();
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Settings window.", ButtonType.OK);
      alert.showAndWait();
    }
  }



}
