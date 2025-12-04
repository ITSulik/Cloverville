package bob.cloverville;

import bob.cloverville.controllers.DashboardController;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class AppContext {

  private static AppContext instance;
  private Stage mainStage;
  private Parent dashboardView;

  private DashboardController dashboardController;

  public DashboardController getDashboardController() {
    return dashboardController;
  }

  public void setDashboardController(DashboardController controller) {
    this.dashboardController = controller;
  }

  private final MemberService memberService;
  private final ActivityService activityService;
  private final UserAccService userAccService;
  private final SettingsService settingsService;

  // Private constructor: initializes all services
  private AppContext() {
    // Initialize storages

    JsonStorage<Activity> greenStorage = new JsonStorage<>(
        "json/green.json",
        new TypeToken<java.util.List<Activity>>() {}.getType()
    );
    JsonStorage<Activity> tradeStorage = new JsonStorage<>(
        "json/trade.json",
        new TypeToken<java.util.List<Activity>>() {}.getType()
    );
    JsonStorage<Activity> communalStorage = new JsonStorage<>(
        "json/communal.json",
        new TypeToken<java.util.List<Activity>>() {}.getType()
    );
    JsonStorage<Settings> settingsStorage = new JsonStorage<>("json/settings.json", Settings.class);
    JsonStorage<UserAccount> userStorage = new JsonStorage<>(
        "json/account.json",
        new TypeToken<java.util.List<UserAccount>>() {}.getType()
    );

    // Initialize services
    this.memberService = new MemberService("json/members.json");
    this.settingsService = new SettingsService(settingsStorage);
    this.activityService = new ActivityService(greenStorage, tradeStorage, communalStorage, memberService, settingsService);
    this.userAccService = new UserAccService(userStorage);
  }

  // Singleton getter
  public static AppContext get() {
    if (instance == null) {
      instance = new AppContext();
    }
    return instance;
  }

  // --- getters for services ---
  public MemberService memberService() { return memberService; }
  public ActivityService activityService() { return activityService; }
  public UserAccService userAccService() { return userAccService; }
  public SettingsService settingsService() { return settingsService; }

  public Stage getMainStage() {
    return mainStage;
  }

  public void setMainStage(Stage stage) {
    this.mainStage = stage;
  }

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

}
