package bob.cloverville;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivityService {

  // --- STORAGE ---
  private final JsonStorage<Activity> greenStorage;
  private final JsonStorage<Activity> tradeStorage;
  private final JsonStorage<Activity> communalStorage;

  // --- IN-MEMORY LISTS ---
  private final List<Activity> greens;
  private final List<Activity> trades;
  private final List<Activity> communal;

  private final MemberService memberService;
  private final SettingsService settingsService;

  public ActivityService(
      JsonStorage<Activity> greenStorage,
      JsonStorage<Activity> tradeStorage,
      JsonStorage<Activity> communalStorage,
      MemberService memberService,
      SettingsService settingsService) {

    this.greenStorage = greenStorage;
    this.tradeStorage = tradeStorage;
    this.communalStorage = communalStorage;

    this.memberService = memberService;
    this.settingsService = settingsService;

    this.greens = new ArrayList<>(greenStorage.load());
    this.trades = new ArrayList<>(tradeStorage.load());
    this.communal = new ArrayList<>(communalStorage.load());
  }

  // ========================================================================
  // ADD ACTIVITY
  // ========================================================================
  public void addActivity(Activity a) {

    // -------- VALIDATION --------
    if (a == null)
      throw new IllegalArgumentException("Activity cannot be null.");

    if (a.getType() == null)
      throw new IllegalArgumentException("Activity type cannot be null.");

    if (getById(a.getId()) != null)
      throw new IllegalStateException("Duplicate activity UUID: " + a.getId());

    // Deadline passed? Remove instantly, no trace.
    if (a.getDeadline() != null && a.getDeadline().isBefore(LocalDate.now()))
      throw new IllegalArgumentException("Activity deadline is in the past and cannot be added.");

    // Trades require at least performer
    if (a.getType() == ActivityType.TRADE_TASK || a.getType() == ActivityType.TRADE_GOODS) {

      if (a.getPerformerID() == null)
        throw new IllegalArgumentException("Trades require a performer.");

      if (memberService.getById(a.getPerformerID()) == null)
        throw new IllegalArgumentException("Performer does not exist.");

      if (a.getReceiverID() != null) {
        if (a.getReceiverID().equals(a.getPerformerID()))
          throw new IllegalArgumentException("Receiver cannot be the performer.");

        if (memberService.getById(a.getReceiverID()) == null)
          throw new IllegalArgumentException("Receiver does not exist.");
      }
    }

    // COMMUNAL: performer must be null, deadline recommended but validated later
    if (a.getType() == ActivityType.COMMUNAL && a.getPerformerID() != null)
      throw new IllegalArgumentException("Communal activities cannot have a performer on creation.");

    // -------- INSERT INTO MEMORY + STORAGE --------
    switch (a.getType()) {
      case GREEN:
        greens.add(a);
        greenStorage.save(greens);
        HistoryWriter.write(a);
        settingsService.addCommunityPoints(a.getPointValue());
        break;

      case TRADE_TASK:
      case TRADE_GOODS:
        trades.add(a);
        tradeStorage.save(trades);
        break;

      case COMMUNAL:
        communal.add(a);
        communalStorage.save(communal);
        break;

      default:
        throw new IllegalArgumentException("Unknown activity type: " + a.getType());
    }
  }


  // ========================================================================
  // DELETE ACTIVITY
  // ========================================================================
  public void deleteActivity(Activity a) {
    if (a == null)
      throw new IllegalArgumentException("Activity cannot be null.");

    switch (a.getType()) {
      case GREEN:
        greens.remove(a);
        greenStorage.save(greens);
        break;

      case TRADE_TASK:
      case TRADE_GOODS:
        trades.remove(a);
        tradeStorage.save(trades);
        break;

      case COMMUNAL:
        communal.remove(a);
        communalStorage.save(communal);
        break;

      default:
        throw new IllegalArgumentException("Unknown activity type: " + a.getType());
    }
  }


  // ========================================================================
  // FETCHERS
  // ========================================================================
  public List<Activity> getGreens() { return new ArrayList<>(greens); }
  public List<Activity> getTrades() { return new ArrayList<>(trades); }
  public List<Activity> getCommunal() { return new ArrayList<>(communal); }
  public List<Activity> getAll(){
    List<Activity> all = new ArrayList<>();
    all.addAll(greens);
    all.addAll(trades);
    all.addAll(communal);
    return all;
  }


  public Activity getById(UUID id) {
    if (id == null) return null;

    for (Activity a : greens) if (a.getId().equals(id)) return a;
    for (Activity a : trades) if (a.getId().equals(id)) return a;
    for (Activity a : communal) if (a.getId().equals(id)) return a;
    return null;
  }


  // ========================================================================
  // COMPLETE ACTIVITY
  // ========================================================================
  public void completeActivity(UUID id) {

    if (id == null)
      throw new IllegalArgumentException("Activity ID cannot be null.");

    Activity a = getById(id);
    if (a == null) return; // silently ignore unknown ID

    // COMMUNAL must have performer before completion
    if (a.getType() == ActivityType.COMMUNAL && a.getPerformerID() == null)
      throw new IllegalArgumentException("Communal activity must have performer before completion.");

    // TRADE must have performer and receiver
    if ((a.getType() == ActivityType.TRADE_TASK || a.getType() == ActivityType.TRADE_GOODS)) {

      if (a.getPerformerID() == null || a.getReceiverID() == null)
        throw new IllegalArgumentException("Trade requires performer and receiver before completion.");

      if (a.getPerformerID().equals(a.getReceiverID()))
        throw new IllegalArgumentException("Trade participants cannot be the same member.");
    }

    a.setCompletedAt(LocalDate.now());
    HistoryWriter.write(a);

    handlePoints(a);

    switch (a.getType()) {
      case TRADE_TASK:
      case TRADE_GOODS:
        trades.remove(a);
        tradeStorage.save(trades);
        break;

      case COMMUNAL:
        communalStorage.save(communal);
        break;

      default:
        break;
    }
  }


  // ========================================================================
  // POINT TRANSFER
  // ========================================================================
  private void handlePoints(Activity a) {
    Member performer =
        a.getPerformerID() != null ? memberService.getById(a.getPerformerID()) : null;

    Member receiver =
        a.getReceiverID() != null ? memberService.getById(a.getReceiverID()) : null;

    switch (a.getType()) {

      case COMMUNAL:
        if (performer != null) {
          performer.addPoints(a.getPointValue());
          performer.incrementTasksCompleted();
          memberService.updateMember(performer);
        }
        break;

      case TRADE_TASK:
      case TRADE_GOODS:
        if (performer != null && receiver != null) {
          receiver.subtractPoints(a.getPointValue());
          performer.addPoints(a.getPointValue());
          memberService.updateMember(receiver);
          memberService.updateMember(performer);
        }
        break;
    }
  }


  // ========================================================================
  // WEEKLY GREEN RESET
  // ========================================================================
  public void weeklyGreenReset() {
    LocalDate now = LocalDate.now();

    greens.removeIf(a ->
        a.getCreatedAt() != null &&
            a.getCreatedAt().plusDays(7).isBefore(now)
    );

    greenStorage.save(greens);
  }


  // ========================================================================
  // UPDATE ACTIVITY
  // ========================================================================
  public void updateActivity(Activity updated) {

    if (updated == null)
      throw new IllegalArgumentException("Updated activity cannot be null.");

    Activity current = getById(updated.getId());
    if (current == null)
      throw new IllegalArgumentException("Activity not found: " + updated.getId());

    if (!current.getType().equals(updated.getType()))
      throw new IllegalArgumentException("Cannot change activity type.");

    // Past-deadline activities cannot remain in system
    if (updated.getDeadline() != null &&
        updated.getDeadline().isBefore(LocalDate.now())) {
      deleteActivity(current);
      return;
    }

    List<Activity> list = getListByType(updated.getType());

    for (Activity a : list) {
      if (a.getId().equals(updated.getId())) {
        a.setTitle(updated.getTitle());
        a.setDescription(updated.getDescription());
        a.setPointValue(updated.getPointValue());
        a.setPerformerID(updated.getPerformerID());
        a.setReceiverID(updated.getReceiverID());
        a.setDeadline(updated.getDeadline());
        break;
      }
    }

    saveList(list, updated.getType());
  }


  // ========================================================================
  // HELPERS
  // ========================================================================
  private List<Activity> getListByType(ActivityType type) {
    return switch (type) {
      case GREEN -> greens;
      case TRADE_TASK, TRADE_GOODS -> trades;
      case COMMUNAL -> communal;
    };
  }

  private void saveList(List<Activity> list, ActivityType type) {
    switch (type) {
      case GREEN -> greenStorage.save(list);
      case TRADE_TASK, TRADE_GOODS -> tradeStorage.save(list);
      case COMMUNAL -> communalStorage.save(list);
    }
  }


  // ========================================================================
  // HISTORY WRITER
  // ========================================================================
  public static class HistoryWriter {

    private static final File file = new File("history.txt");

    public static void write(Activity a) {
      try (FileWriter w = new FileWriter(file, true)) {
        w.write(format(a) + System.lineSeparator());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private static String format(Activity a) {
      return String.format(
          "[%s] %s | %s | %s → %s | %d points",
          a.getCreatedAt(),
          a.getType(),
          a.getTitle(),
          a.getPerformerID(),
          a.getReceiverID(),
          a.getPointValue()
      );
    }
  }

}
