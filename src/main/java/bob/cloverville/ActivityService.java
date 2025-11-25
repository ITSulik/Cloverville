package bob.cloverville;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivityService {

  private final List<Activity> activities;
  private final List<Activity> history;
  private final JsonStorage<Activity> activityStorage;
  private final JsonStorage<Activity> historyStorage;
  private final MemberService memberService;
  private final SettingsService settingsService;

  public ActivityService(JsonStorage<Activity> activityStorage,
      JsonStorage<Activity> historyStorage,
      MemberService memberService, SettingsService settingsService) {
    this.activityStorage = activityStorage;
    this.historyStorage = historyStorage;
    this.memberService = memberService;

    this.activities = new ArrayList<>(activityStorage.load());
    this.history = new ArrayList<>(historyStorage.load());
    this.settingsService = settingsService;
  }

  // ---------------------- CRUD ----------------------
  public void addActivity(Activity a) {
    activities.add(a);
    saveActivities();
  }

  public void deleteActivity(Activity a) {
    activities.remove(a);
    saveActivities();
  }

  public Activity getById(UUID id) {
    for (Activity a : activities) {
      if (a.getId().equals(id)) return a;
    }
    return null;
  }

  public List<Activity> getAll() {
    return new ArrayList<>(activities);
  }

  public List<Activity> getByType(ActivityType type) {
    List<Activity> list = new ArrayList<>();
    for (Activity a : activities) {
      if (a.getType() == type) list.add(a);
    }
    return list;
  }

  // ---------------------- Completion ----------------------
  public void completeActivity(UUID activityId) {
    Activity a = getById(activityId);
    if (a == null) return;

    // Mark completed
    a.setCompletedAt(LocalDate.now());
    history.add(a);
    saveHistory();

    // Handle points
    handlePoints(a);

    // Delete unless COMMUNAL
    if (a.getType() != ActivityType.COMMUNAL) {
      activities.remove(a);
    }

    saveActivities();
  }

  private void handlePoints(Activity a) {
    Member performer = a.getPerformerID() != null ? memberService.getById(a.getPerformerID()) : null;
    Member receiver = a.getReceiverID() != null ? memberService.getById(a.getReceiverID()) : null;

    switch (a.getType()) {
      case GREEN:
        settingsService.addCommunityPoints(a.getPointValue());
        break;

      case COMMUNAL:
        if (performer != null) {
          performer.addPoints(a.getPointValue());
          performer.incrementTasksCompleted();
        }
        break;

      case TRADE_TASK:
      case TRADE_GOODS:
        if (performer != null && receiver != null) {
          receiver.subtractPoints(a.getPointValue());
          performer.addPoints(a.getPointValue());
        }
        break;
    }
  }

  // ---------------------- History ----------------------
  public List<Activity> getCompletedThisWeek() {
    List<Activity> list = new ArrayList<>();
    LocalDate weekStart = LocalDate.now().minus(7, ChronoUnit.DAYS);
    for (Activity a : history) {
      if (a.getCompletedAt() != null && a.getCompletedAt().isAfter(weekStart)) {
        list.add(a);
      }
    }
    return list;
  }

  // ---------------------- Point Bonuses ----------------------
  public void applyParticipationBonus(int minTasks, int bonusPercent) {
    for (Member m : memberService.getAll()) {
      if (m.getTotalTasksCompleted() < minTasks) {
        int bonus = (int) Math.ceil(m.getPersonalPoints() * bonusPercent / 100.0);
        m.addPoints(bonus);
      }
    }
    memberService.getAll().forEach(memberService::updateMember); // save updated points
  }

  // ---------------------- Point Reset ----------------------
  public void manualReset(int resetPoints) {
    for (Member m : memberService.getAll()) {
      m.setPoints(resetPoints);
    }
    memberService.getAll().forEach(memberService::updateMember);
  }

  public void sixMonthReset() {
    LocalDate now = LocalDate.now();
    for (Member m : memberService.getAll()) {
      m.setPoints(10); // default reset value
    }
    memberService.getAll().forEach(memberService::updateMember);
  }

  // ---------------------- JSON Save ----------------------
  private void saveActivities() {
    activityStorage.save(activities);
  }

  private void saveHistory() {
    historyStorage.save(history);
  }

}
