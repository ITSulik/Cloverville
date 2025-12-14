package bob.cloverville;

import java.util.UUID;

public class Member {

  private final UUID id;
  private String name;
  private int personalPoints;
  private int totalTasksCompleted;

  public Member(String name, int points, int tasksCompleted) {
    this.id = UUID.randomUUID();
    setName(name);
    setPoints(points);
    setTotalTasksCompleted(tasksCompleted);
  }

  public UUID getId() { return id; }
  public String getName() { return name; }
  public int getPersonalPoints() { return personalPoints; }
  public int getTotalTasksCompleted() { return totalTasksCompleted; }

  public void setName(String name) {
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Name cannot be empty");

    if (!name.matches("[a-zA-Z0-9 ]+( \\([0-9]+\\))?"))
      throw new IllegalArgumentException("Name cannot contain special characters");

    this.name = name;
  }

  public void setPoints(int points) {
    if (points < 0)
      throw new IllegalArgumentException("Points cannot be negative");
    this.personalPoints = points;
  }

  public void setTotalTasksCompleted(int tasksCompleted) {
    if (tasksCompleted < 0)
      throw new IllegalArgumentException("Tasks completed cannot be negative");
    this.totalTasksCompleted = tasksCompleted;
  }

  public void incrementTasksCompleted() {
    totalTasksCompleted++;
  }

  public void addPoints(int amount) {
    if (amount < 0)
      throw new IllegalArgumentException("Points to add must be non-negative");
    personalPoints += amount;
  }

  public void subtractPoints(int amount) {
    if (amount < 0)
      throw new IllegalArgumentException("Points to subtract must be non-negative");
    personalPoints = Math.max(0, personalPoints - amount);
  }
}
