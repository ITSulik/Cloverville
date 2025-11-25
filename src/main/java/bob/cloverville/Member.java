package bob.cloverville;

import java.util.UUID;

public class Member {
  private final UUID id;
  private String name;
  private int personalPoints;
  private int totalTasksCompleted;

  public Member(String name) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.personalPoints = 10; // Starting points
    this.totalTasksCompleted = 0;
  }

  public UUID getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public int getPersonalPoints()
  {
    return personalPoints;
  }

  public int getTotalTasksCompleted()
  {
    return totalTasksCompleted;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setPoints(int personalPoints)
  {
    this.personalPoints = personalPoints;
  }

  public void setTotalTasksCompleted(int totalTasksCompleted)
  {
    this.totalTasksCompleted = totalTasksCompleted;
  }

  public void incrementTasksCompleted() {
    this.totalTasksCompleted++;
  }

  public void addPoints(int amount) {
    this.personalPoints += amount;
  }

  public void subtractPoints(int amount) {
    this.personalPoints = Math.max(0, this.personalPoints - amount);
  }

  public void applyBonus(double percent) {
    int bonus = (int) Math.round(personalPoints * percent);
    this.personalPoints += bonus;
  }

}
