package bob.cloverville;

import java.time.LocalDate;

public class Settings {

  private int communityPoints;
  private LocalDate lastResetDate;
  private int lowParticipationThreshold; // minimum tasks to qualify for bonus
  private int bonusPercent;              // e.g., 10–30%

  // Default constructor for JSON deserialization
  public Settings() {
    this.communityPoints = 0;
    this.lastResetDate = LocalDate.now();
    this.lowParticipationThreshold = 3; // example default
    this.bonusPercent = 20;             // example default
  }

  // -------- Getters --------
  public int getCommunityPoints() { return communityPoints; }
  public LocalDate getLastResetDate() { return lastResetDate; }
  public int getLowParticipationThreshold() { return lowParticipationThreshold; }
  public int getBonusPercent() { return bonusPercent; }

  // -------- Setters --------
  public void setCommunityPoints(int communityPoints) { this.communityPoints = communityPoints; }
  public void setLastResetDate(LocalDate lastResetDate) { this.lastResetDate = lastResetDate; }
  public void setLowParticipationThreshold(int lowParticipationThreshold) { this.lowParticipationThreshold = lowParticipationThreshold; }
  public void setBonusPercent(int bonusPercent) { this.bonusPercent = bonusPercent; }

  // Convenience method to add points to the pool
  public void addCommunityPoints(int points) {
    this.communityPoints += points;
  }
}
