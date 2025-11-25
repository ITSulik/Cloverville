package bob.cloverville;

import java.time.LocalDate;
import java.util.UUID;

public class Activity {

  private final UUID id;
  private String title;
  private String description;
  private int pointValue;
  private UUID performerID; // null for GREEN type (points go to community)
  private UUID receiverID;  // null for GREEN and COMMUNAL types
  private final LocalDate createdAt;
  private LocalDate deadline;     // optional, can be null
  private LocalDate completedAt;  // null until completed
  private ActivityType type;

  // -------- Constructor --------
  public Activity(String title, String description, int pointValue,
      UUID performerID, UUID receiverID, ActivityType type,
      LocalDate deadline) {
    this.id = UUID.randomUUID();
    this.title = title;
    this.description = description;
    this.pointValue = pointValue;
    this.performerID = performerID;
    this.receiverID = receiverID;
    this.type = type;
    this.createdAt = LocalDate.now();
    this.deadline = deadline;
    this.completedAt = null;
  }

  // -------- Getters --------
  public UUID getId() { return id; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public int getPointValue() { return pointValue; }
  public UUID getPerformerID() { return performerID; }
  public UUID getReceiverID() { return receiverID; }
  public LocalDate getCreatedAt() { return createdAt; }
  public LocalDate getDeadline() { return deadline; }
  public LocalDate getCompletedAt() { return completedAt; }
  public ActivityType getType() { return type; }

  // -------- Setters --------
  public void setTitle(String title) { this.title = title; }
  public void setDescription(String description) { this.description = description; }
  public void setPointValue(int pointValue) { this.pointValue = pointValue; }
  public void setPerformerID(UUID performerID) { this.performerID = performerID; }
  public void setReceiverID(UUID receiverID) { this.receiverID = receiverID; }
  public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
  public void setCompletedAt(LocalDate completedAt) { this.completedAt = completedAt; }
  public void setType(ActivityType type) { this.type = type; }

}
