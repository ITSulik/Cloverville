package bob.cloverville;

import bob.cloverville.controllers.DashboardController;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;

public class AppContext {

  private static AppContext instance;  // Singleton instance

  private Stage mainStage;
  private Parent dashboardView;
  private DashboardController dashboardController;

  private final MemberService memberService;
  private final ActivityService activityService;
  private final UserAccService userAccService;
  private final SettingsService settingsService;

  private UserAccount currentUser;

  // Reusable generic list types to avoid recreating TypeTokens.
  private static final Type ACTIVITY_LIST_TYPE =
      new TypeToken<java.util.List<Activity>>() {}.getType();

  private static final Type USER_LIST_TYPE =
      new TypeToken<java.util.List<UserAccount>>() {}.getType();

  // Constructor initializes all services
  private AppContext() {

    JsonStorage<Activity> greenStorage =
        new JsonStorage<>("website/json/green.json", ACTIVITY_LIST_TYPE);

    JsonStorage<Activity> tradeStorage =
        new JsonStorage<>("website/json/trade.json", ACTIVITY_LIST_TYPE);

    JsonStorage<Activity> communalStorage =
        new JsonStorage<>("website/json/communal.json", ACTIVITY_LIST_TYPE);

    JsonStorage<Settings> settingsStorage =
        new JsonStorage<>("website/json/settings.json", Settings.class);

    JsonStorage<UserAccount> userStorage =
        new JsonStorage<>("website/json/account.json", USER_LIST_TYPE);

    this.memberService = new MemberService("website/json/members.json");
    this.settingsService = new SettingsService(settingsStorage);
    this.activityService = new ActivityService(
        greenStorage,
        tradeStorage,
        communalStorage,
        memberService,
        settingsService
    );
    this.userAccService = new UserAccService(userStorage);
  }

  // Thread-safe Singleton getter (optional but cleaner)
  public static synchronized AppContext get() {
    if (instance == null) {
      instance = new AppContext();
    }
    return instance;
  }

  // -----------------
  // Service Getters
  // -----------------
  public MemberService memberService() {
    return memberService;
  }

  public ActivityService activityService() {
    return activityService;
  }

  public UserAccService userAccService() {
    return userAccService;
  }

  public SettingsService settingsService() {
    return settingsService;
  }

  // -----------------
  // Dashboard Loading
  // -----------------
  public Parent getDashboard() {
    if (dashboardView == null) {
      dashboardView = loadView("dashboard.fxml");
    }
    return dashboardView;
  }

  private Parent loadView(String fxml) {
    try {
      return FXMLLoader.load(getClass().getResource("/" + fxml));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load: " + fxml, e);
    }
  }

  // -----------------
  // Stage & Controller
  // -----------------
  public Stage getMainStage() {
    return mainStage;
  }

  public void setMainStage(Stage stage) {
    this.mainStage = stage;
  }

  public DashboardController getDashboardController() {
    return dashboardController;
  }

  public void setDashboardController(DashboardController controller) {
    this.dashboardController = controller;
  }

  // -----------------
  // User Session
  // -----------------
  public void setCurrentUser(UserAccount user) {
    this.currentUser = user;
  }

  public UserAccount getCurrentUser() {
    return currentUser;
  }
}
