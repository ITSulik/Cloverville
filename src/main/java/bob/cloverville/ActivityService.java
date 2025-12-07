package bob.cloverville;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ActivityService {

  private final JsonStorage<Activity> greenStorage;
  private final JsonStorage<Activity> tradeStorage;
  private final JsonStorage<Activity> communalStorage;

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

    if (a == null) throw new IllegalArgumentException("Activity cannot be null.");
    if (a.getType() == null) throw new IllegalArgumentException("Activity type cannot be null.");

    if (getById(a.getId()) != null)
      throw new IllegalStateException("Duplicate activity UUID: " + a.getId());

    if (a.getDeadline() != null && a.getDeadline().isBefore(LocalDate.now()))
      throw new IllegalArgumentException("Activity deadline is in the past and cannot be added.");

    // Trade validation
    if (a.getType() == ActivityType.TRADE_TASK || a.getType() == ActivityType.TRADE_GOODS) {

      UUID performerID = a.getPerformerID();

      if (performerID == null)
        throw new IllegalArgumentException("Trades require a performer.");

      if (memberService.getById(performerID) == null)
        throw new IllegalArgumentException("Performer does not exist.");

    }

    if (a.getType() == ActivityType.COMMUNAL && a.getPerformerID() != null)
      throw new IllegalArgumentException("Communal activities cannot have a performer on creation.");

    // Insert + save
    List<Activity> list = getListByType(a.getType());
    list.add(a);
    saveList(list, a.getType());

    if (a.getType() == ActivityType.GREEN) {
      HistoryWriter.write(a);
      settingsService.addCommunityPoints(a.getPointValue());
    }
  }

  // ========================================================================
  // DELETE ACTIVITY
  // ========================================================================
  public void deleteActivity(Activity a) {
    if (a == null) throw new IllegalArgumentException("Activity cannot be null.");

    List<Activity> list = getListByType(a.getType());
    list.remove(a);
    saveList(list, a.getType());
  }

  // ========================================================================
  // FETCHERS
  // ========================================================================
  public List<Activity> getGreens() { return new ArrayList<>(greens); }
  public List<Activity> getTrades() { return new ArrayList<>(trades); }
  public List<Activity> getCommunal() { return new ArrayList<>(communal); }

  public List<Activity> getAll() {
    int total = greens.size() + trades.size() + communal.size();
    List<Activity> all = new ArrayList<>(total);
    all.addAll(greens);
    all.addAll(trades);
    all.addAll(communal);
    return all;
  }

  public Activity getById(UUID id) {
    if (id == null) return null;

    for (Activity a : greens)    if (a.getId().equals(id)) return a;
    for (Activity a : trades)    if (a.getId().equals(id)) return a;
    for (Activity a : communal)  if (a.getId().equals(id)) return a;

    return null;
  }

  // ========================================================================
  // COMPLETE ACTIVITY
  // ========================================================================
  public void completeActivity(UUID id) {

    if (id == null) throw new IllegalArgumentException("Activity ID cannot be null.");

    Activity a = getById(id);
    if (a == null) return;

    ActivityType type = a.getType();

    // Communal
    if (type == ActivityType.COMMUNAL && a.getPerformerID() == null)
      throw new IllegalArgumentException("Communal activity must have performer before completion.");

    // Trade logic
    if (type == ActivityType.TRADE_TASK || type == ActivityType.TRADE_GOODS) {

      UUID performerID = a.getPerformerID();
      UUID receiverID = a.getReceiverID();

      if (performerID == null || receiverID == null)
        throw new IllegalArgumentException("Trade requires performer and receiver before completion.");

      if (performerID.equals(receiverID))
        throw new IllegalArgumentException("Trade participants cannot be the same member.");

      Member performer = memberService.getById(performerID);
      Member receiver = memberService.getById(receiverID);

      if (type == ActivityType.TRADE_TASK && performer.getPersonalPoints() < a.getPointValue())
        throw new IllegalArgumentException("Performer doesn't have enough points.");

      if (type == ActivityType.TRADE_GOODS && receiver.getPersonalPoints() < a.getPointValue())
        throw new IllegalArgumentException("Receiver doesn't have enough points.");
    }

    a.setCompletedAt(LocalDate.now());
    HistoryWriter.write(a);

    handlePoints(a);

    if (type == ActivityType.TRADE_TASK || type == ActivityType.TRADE_GOODS) {
      trades.remove(a);
      tradeStorage.save(trades);
    }
    else if (type == ActivityType.COMMUNAL) {
      communalStorage.save(communal);
    }
  }

  // ========================================================================
  // POINT TRANSFER
  // ========================================================================
  private void handlePoints(Activity a) {

    Member performer = a.getPerformerID() != null ? memberService.getById(a.getPerformerID()) : null;
    Member receiver  = a.getReceiverID() != null ? memberService.getById(a.getReceiverID()) : null;

    switch (a.getType()) {

      case COMMUNAL -> {
        performer.addPoints(a.getPointValue());
        performer.incrementTasksCompleted();
        memberService.updateMember(performer);
      }

      case TRADE_TASK -> {
        receiver.addPoints(a.getPointValue());
        performer.subtractPoints(a.getPointValue());
        receiver.incrementTasksCompleted();
        memberService.updateMember(receiver);
        memberService.updateMember(performer);
      }

      case TRADE_GOODS -> {
        receiver.subtractPoints(a.getPointValue());
        performer.addPoints(a.getPointValue());
        memberService.updateMember(receiver);
        memberService.updateMember(performer);
      }
    }
  }

  // ========================================================================
  // WEEKLY GREEN RESET
  // ========================================================================
  public void weeklyGreenReset() {
    LocalDate now = LocalDate.now();
    greens.removeIf(a -> a.getCreatedAt() != null && a.getCreatedAt().plusDays(7).isBefore(now));
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

    if (current.getType() != updated.getType())
      throw new IllegalArgumentException("Cannot change activity type.");

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
        w.write(format(a, AppContext.get().memberService()) + System.lineSeparator());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private static String format(Activity a, MemberService memberService) {
      return String.format(
          "[%s] %s | %s | %s → %s | %d points",
          a.getCreatedAt(),
          a.getType(),
          a.getTitle(),
          memberService.getNameById(a.getPerformerID()),
          memberService.getNameById(a.getReceiverID()),
          a.getPointValue()
      );
    }
  }
}
