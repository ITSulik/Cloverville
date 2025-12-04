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
    private final ActivityType type;

    private static final int TITLE_MAX = 50;
    private static final int DESCRIPTION_MAX = 300;

    // -------- Constructor --------
    public Activity(String title, String description, int pointValue,
        UUID performerID, UUID receiverID, ActivityType type,
        LocalDate deadline) {
      this.id = UUID.randomUUID();
      this.createdAt = LocalDate.now();
      this.type = validateType(type);

      this.title = validateTitle(title);
      this.description = validateDescription(description);
      this.pointValue = validatePoints(pointValue);

      validateIDsByType(type, performerID, receiverID);
      this.performerID = performerID;
      this.receiverID = receiverID;

      this.deadline = validateDeadline(deadline);
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

    private ActivityType validateType(ActivityType type) {
      if (type == null)
        throw new IllegalArgumentException("Activity type cannot be null.");
      return type;
    }

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
          if (performer != null)
            throw new IllegalArgumentException("GREEN activities must not have a performer.");
          if (receiver != null)
            throw new IllegalArgumentException("GREEN activities must not have a receiver.");
          break;

        case TRADE_TASK:
        case TRADE_GOODS:
          if (performer == null)
            throw new IllegalArgumentException("TRADE activities must have a performer.");
          // receiver may be null at creation
          break;

        case COMMUNAL:
          if (performer != null || receiver != null)
            throw new IllegalArgumentException("COMMUNAL activities cannot have performer or receiver initially.");
          break;

        default:
          throw new IllegalStateException("Unexpected activity type.");
      }
    }

    private LocalDate validateDeadline(LocalDate deadline) {
      // GREEN + TRADE allow null
      if (type == ActivityType.GREEN || type == ActivityType.TRADE_TASK || type == ActivityType.TRADE_GOODS) {
        if (deadline == null) return null;
      }

      // COMMUNAL requires future deadline
      if (type == ActivityType.COMMUNAL && deadline == null)
        throw new IllegalArgumentException("COMMUNAL activities must have a deadline.");

      if (deadline != null && !deadline.isAfter(createdAt))
        throw new IllegalArgumentException("Deadline must be in the future.");

      return deadline;
    }


    private LocalDate validateCompletedAt(LocalDate completedAt) {
      if (completedAt == null)
        throw new IllegalArgumentException("Completion date cannot be null.");
      if (completedAt.isBefore(createdAt))
        throw new IllegalArgumentException("Completion date cannot be before creation date.");
      if (deadline != null && completedAt.isAfter(deadline))
        throw new IllegalArgumentException("Cannot complete after deadline has passed.");
      return completedAt;
    }
  }
