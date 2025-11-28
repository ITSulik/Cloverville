package bob.cloverville;

import java.util.UUID;

public class UserAccount {

  private UUID id;
  private String username;
  private String passwordHash;

  public UserAccount(String username, String passwordHash) {
    setUsername(username);
    setPasswordHash(passwordHash);
    this.id = UUID.randomUUID();
  }

  private void validateUsername(String name) {
    if (name == null)
      throw new IllegalArgumentException("Username cannot be null.");
    if (name.isBlank())
      throw new IllegalArgumentException("Username cannot be blank.");
    if (name.length() < 3 || name.length() > 30)
      throw new IllegalArgumentException("Username must be 3–30 characters.");
    if (!name.matches("^\\S+$"))
      throw new IllegalArgumentException("Username cannot contain spaces.");
  }

  private void validatePassword(String hash) {
    if (hash == null)
      throw new IllegalArgumentException("Password hash cannot be null.");
    if (hash.isBlank())
      throw new IllegalArgumentException("Password hash cannot be blank.");
    if (hash.length() < 5)
      throw new IllegalArgumentException("Password hash must be at least 5 characters.");
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setUsername(String username) {
    validateUsername(username);
    this.username = username;
  }

  public void setPasswordHash(String passwordHash) {
    validatePassword(passwordHash);
    this.passwordHash = passwordHash;
  }
}
