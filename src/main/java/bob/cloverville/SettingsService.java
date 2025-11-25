package bob.cloverville;

public class SettingsService {

  private final JsonStorage<Settings> storage;
  private Settings settings;

  public SettingsService(JsonStorage<Settings> storage) {
    this.storage = storage;
    this.settings = storage.loadSingle();
    if (this.settings == null) {
      this.settings = new Settings();
      save();
    }
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
  public void setBonusRules(int lowParticipationThreshold, int bonusPercent) {
    settings.setLowParticipationThreshold(lowParticipationThreshold);
    settings.setBonusPercent(bonusPercent);
    save();
  }
}
