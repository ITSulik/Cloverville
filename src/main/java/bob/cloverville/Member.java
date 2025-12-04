package bob.cloverville;

import java.util.UUID;

public class Member {

  private UUID id;
  private String name;
  private int personalPoints;
  private int totalTasksCompleted;

  public Member(String name, int p, int t) {
    this.id = UUID.randomUUID();
    setName(name); // validate on creation
    this.personalPoints = p; // Starting points
    this.totalTasksCompleted = t;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getPersonalPoints() {
    return personalPoints;
  }

  public int getTotalTasksCompleted() {
    return totalTasksCompleted;
  }

  public void setName(String name) {
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Name cannot be empty");
    if (!name.matches("[a-zA-Z0-9 ]+"))
      throw new IllegalArgumentException("Name cannot contain special characters like $#%.");
    this.name = name;
  }

  public void setPoints(int personalPoints) {
    if (personalPoints < 0)
      throw new IllegalArgumentException("Points cannot be negative");
    this.personalPoints = personalPoints;
  }

  public void setTotalTasksCompleted(int totalTasksCompleted) {
    if (totalTasksCompleted < 0)
      throw new IllegalArgumentException("Total tasks completed cannot be negative");
    this.totalTasksCompleted = totalTasksCompleted;
  }

  public void incrementTasksCompleted() {
    this.totalTasksCompleted++;
  }

  public void addPoints(int amount) {
    if (amount < 0)
      throw new IllegalArgumentException("Points to add must be zero or positive");
    this.personalPoints += amount;
  }

  public void subtractPoints(int amount) {
    if (amount < 0)
      throw new IllegalArgumentException("Points to subtract must be zero or positive");
    this.personalPoints = Math.max(0, this.personalPoints - amount);
  }
}
