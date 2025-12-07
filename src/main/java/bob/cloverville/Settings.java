package bob.cloverville;

import java.time.LocalDate;

public class Settings {

  private int communityPoints;
  private LocalDate lastResetDate;
  private LocalDate pointResetDate;

  private String communityGoal;
  private int targetPoints;

  // Default constructor for JSON deserialization
  public Settings(int points, String goal, int targetPoints) {
    this.communityPoints = points;
    this.lastResetDate = LocalDate.now();
    this.communityGoal = goal;
    this.targetPoints = targetPoints;
    this.pointResetDate = LocalDate.now();
  }

  // -------- Getters --------
  public int getCommunityPoints() { return communityPoints; }
  public LocalDate getLastResetDate() { return lastResetDate; }
  public String getCommunityGoal() { return communityGoal; }
  public int getTargetPoints() { return targetPoints; }
  public LocalDate getPointResetDate() { return pointResetDate; }

  // -------- Setters --------
  public void setCommunityPoints(int communityPoints) { this.communityPoints = communityPoints; }
  public void setLastResetDate(LocalDate lastResetDate) { this.lastResetDate = lastResetDate; }
  public void setCommunityGoal(String communityGoal) { this.communityGoal = communityGoal; }
  public void setTargetPoints(int targetPoints) { this.targetPoints = targetPoints; }
  public void setPointResetDate(LocalDate pointResetDate) { this.pointResetDate = pointResetDate; }

  // Convenience method to add points to the pool
  public void addCommunityPoints(int points) {
    this.communityPoints += points;
  }
}
