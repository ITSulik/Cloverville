package bob.cloverville;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserAccService {

  private final List<UserAccount> accounts;
  private final JsonStorage<UserAccount> storage;

  public UserAccService(JsonStorage<UserAccount> storage) {
    this.storage = storage;
    this.accounts = new ArrayList<>(storage.load());
  }

  // ---------- AUTHENTICATION ----------
  public UserAccount authenticate(String username, String passwordHash) {

    if (username == null || passwordHash == null || username.isBlank() || passwordHash.isBlank())
      throw new IllegalArgumentException("Username and Password cannot be blank.");

    for (UserAccount acc : accounts) {
      if (acc.getUsername().equals(username)
          && acc.getPasswordHash().equals(passwordHash)) {
        return acc;
      }
    }
    return null;
  }

  // ---------- ADD USER ----------
  public void addUser(UserAccount account) {

    if (account == null)
      throw new IllegalArgumentException("UserAccount cannot be null.");

    if (existsByUsername(account.getUsername()))
      throw new IllegalArgumentException("Username already exists.");

    accounts.add(account);
    save();
  }

  private boolean existsByUsername(String username) {
    for (UserAccount acc : accounts) {
      if (acc.getUsername().equalsIgnoreCase(username))
        return true;
    }
    return false;
  }

  // ---------- DELETE USER ----------
  public void deleteUser(UserAccount account) {
    accounts.remove(account);
    save();
  }

  // ---------- FIND ALL ----------
  public List<UserAccount> getAll() {
    return new ArrayList<>(accounts);
  }

  // ---------- FIND BY ID ----------
  public UserAccount getById(UUID id) {
    if (id == null)
      throw new IllegalArgumentException("ID cannot be null.");

    for (UserAccount acc : accounts) {
      if (acc.getId().equals(id))
        return acc;
    }
    return null;
  }

  // ---------- FIND BY USERNAME ----------
  public UserAccount getByUsername(String username) {
    if (username == null)
      throw new IllegalArgumentException("Username cannot be null.");
    if (username.isBlank())
      return null; // rule #10: reject blank search and return null

    for (UserAccount acc : accounts) {
      if (acc.getUsername().equalsIgnoreCase(username))
        return acc;
    }
    return null;
  }

  // ---------- UPDATE ----------
  public void changePassword(UserAccount user, String newHash) {
    if (user == null)
      throw new IllegalArgumentException("User must not be null.");
    if (newHash == null)
      throw new IllegalArgumentException("New password hash cannot be null.");

    user.setPasswordHash(newHash);
    save();
  }

  public void changeUsername(UserAccount user, String newName) {
    if (user == null)
      throw new IllegalArgumentException("User must not be null.");
    if (newName == null)
      throw new IllegalArgumentException("New username cannot be null.");

    if (existsByUsername(newName))
      throw new IllegalArgumentException("Username already exists.");

    user.setUsername(newName);
    save();
  }

  // ---------- SAVE TO JSON ----------
  private void save() {
    storage.save(accounts);
  }
}
