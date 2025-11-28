package bob.cloverville;

import java.time.LocalDate;

public class Settings {

  private int communityPoints;
  private LocalDate lastResetDate;

  // Default constructor for JSON deserialization
  public Settings() {
    this.communityPoints = 0;
    this.lastResetDate = LocalDate.now();
  }

  // -------- Getters --------
  public int getCommunityPoints() { return communityPoints; }
  public LocalDate getLastResetDate() { return lastResetDate; }

  // -------- Setters --------
  public void setCommunityPoints(int communityPoints) { this.communityPoints = communityPoints; }
  public void setLastResetDate(LocalDate lastResetDate) { this.lastResetDate = lastResetDate; }

  // Convenience method to add points to the pool
  public void addCommunityPoints(int points) {
    this.communityPoints += points;
  }


}
