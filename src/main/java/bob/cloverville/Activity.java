package bob.cloverville;

import java.time.LocalDate;
import java.util.UUID;

public class Activity {

  private final UUID id;
  private final ActivityType type;

  private String title;
  private String description;
  private int pointValue;

  private UUID performerID;
  private UUID receiverID;

  private LocalDate createdAt;
  private LocalDate deadline;
  private LocalDate completedAt;

  private static final int TITLE_MAX = 50;
  private static final int DESCRIPTION_MAX = 300;

  // -------- Constructor --------
  public Activity(String title, String description, int pointValue,
      UUID performerID, UUID receiverID,
      ActivityType type, LocalDate deadline) {

    if (type == null)
      throw new IllegalArgumentException("Activity type cannot be null.");

    this.id = UUID.randomUUID();
    this.createdAt = LocalDate.now();
    this.type = type;

    this.title = validateTitle(title);
    this.description = validateDescription(description);
    this.pointValue = validatePoints(pointValue);

    validateIDsByType(type, performerID, receiverID);
    this.performerID = performerID;
    this.receiverID = receiverID;

    this.deadline = validateDeadline(type, deadline, this.createdAt);
    this.completedAt = null;
  }

  // -------- Getters --------
  public UUID getId() { return id; }
  public ActivityType getType() { return type; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public int getPointValue() { return pointValue; }
  public UUID getPerformerID() { return performerID; }
  public UUID getReceiverID() { return receiverID; }
  public LocalDate getCreatedAt() { return createdAt; }
  public LocalDate getDeadline() { return deadline; }
  public LocalDate getCompletedAt() { return completedAt; }

  // -------- Setters --------
  public void setTitle(String title) { this.title = validateTitle(title); }
  public void setDescription(String description) { this.description = validateDescription(description); }
  public void setPointValue(int pointValue) { this.pointValue = validatePoints(pointValue); }
  public void setPerformerID(UUID performerID) { this.performerID = performerID; }
  public void setReceiverID(UUID receiverID) { this.receiverID = receiverID; }
  public void setDeadline(LocalDate deadline) { this.deadline = validateDeadline(type, deadline, createdAt); }
  public void setCompletedAt(LocalDate completedAt) { this.completedAt = completedAt; }
  public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

  // -------- Validation --------
  private String validateTitle(String title) {
    if (title == null || title.isBlank())
      throw new IllegalArgumentException("Title cannot be empty.");
    if (title.length() > TITLE_MAX)
      throw new IllegalArgumentException("Title cannot exceed " + TITLE_MAX + " characters.");
    return title;
  }

  private String validateDescription(String description) {
    if (description == null || description.isBlank())
      throw new IllegalArgumentException("Description cannot be empty.");
    if (description.length() > DESCRIPTION_MAX)
      throw new IllegalArgumentException("Description exceeds " + DESCRIPTION_MAX + " characters.");
    return description;
  }

  private int validatePoints(int points) {
    if (points < 0)
      throw new IllegalArgumentException("Point value cannot be negative.");
    return points;
  }

  private void validateIDsByType(ActivityType type, UUID performer, UUID receiver) {
    switch (type) {
      case GREEN:
        if (performer != null || receiver != null)
          throw new IllegalArgumentException("GREEN activities cannot have performer or receiver.");
        break;

      case TRADE_TASK:
      case TRADE_GOODS:
        if (performer == null)
          throw new IllegalArgumentException("TRADE activities must have a performer.");
        // receiver may be null
        break;

      case COMMUNAL:
        if (performer != null || receiver != null)
          throw new IllegalArgumentException("COMMUNAL activities cannot have performer or receiver.");
        break;

      default:
        throw new IllegalStateException("Unexpected activity type.");
    }
  }

  private LocalDate validateDeadline(ActivityType type, LocalDate deadline, LocalDate created) {
    // COMMUNAL requires future deadline
    if (type == ActivityType.COMMUNAL) {
      if (deadline == null)
        throw new IllegalArgumentException("COMMUNAL activities must have a deadline.");
      if (!deadline.isAfter(created))
        throw new IllegalArgumentException("Deadline must be in the future.");
      return deadline;
    }

    // All other types allow null or future
    if (deadline != null && !deadline.isAfter(created))
      throw new IllegalArgumentException("Deadline must be in the future.");
    return deadline;
  }
}
