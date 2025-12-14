package bob.cloverville;

import java.time.LocalDate;

public class SettingsService {

  private final JsonStorage<Settings> storage;
  private Settings settings;

  public SettingsService(JsonStorage<Settings> storage) {
    this.storage = storage;
    this.settings = storage.loadSingle();

  }

  // Get the current settings object
  public Settings getSettings() {
    return settings;
  }

  // Save current settings to JSON
  public void save() {
    storage.saveSingle(settings);
  }

  // Add points to community pool and save
  public void addCommunityPoints(int points) {
    settings.addCommunityPoints(points);
    save();
  }

  // Update last reset date
  public void updateLastResetDate() {
    settings.setLastResetDate(java.time.LocalDate.now());
    save();
  }

  // Update bonus rules
  public boolean isWeeklyResetDue() {
    return settings.getLastResetDate().plusWeeks(1).isBefore(LocalDate.now()) || settings.getLastResetDate().plusWeeks(1).isEqual(LocalDate.now());
  }

  public boolean isMonthlyPointResetDue() {
    return settings.getPointResetDate() != null &&
        (settings.getPointResetDate().plusMonths(6).isBefore(LocalDate.now()) ||
         settings.getPointResetDate().plusMonths(6).isEqual(LocalDate.now()));
  }

  public void updatePointResetDate() {
    settings.setPointResetDate(LocalDate.now());
    save();
  }
}
