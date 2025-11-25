package bob.cloverville;

import java.util.UUID;

public class UserAccount {
  private final UUID id;
  private String username;
  private String passwordHash;

  public UserAccount(String username, String passwordHash) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.passwordHash = passwordHash;
  }

  public UUID getId()
  {
    return id;
  }

  public String getPasswordHash()
  {
    return passwordHash;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public void setPasswordHash(String passwordHash)
  {
    this.passwordHash = passwordHash;
  }
}

