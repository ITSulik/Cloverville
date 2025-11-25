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
    for (UserAccount acc : accounts) {
      if (acc.getUsername().equals(username)
          && acc.getPasswordHash().equals(passwordHash)) {
        return acc;
      }
    }
    return null;
  }

  // ---------- CRUD ----------
  public void addUser(UserAccount account) {
    accounts.add(account);
    save();
  }

  public void deleteUser(UserAccount account) {
    accounts.remove(account);
    save();
  }

  public List<UserAccount> getAll() {
    return new ArrayList<>(accounts);
  }

  public UserAccount getById(UUID id) {
    for (UserAccount acc : accounts) {
      if (acc.getId().equals(id))
        return acc;
    }
    return null;
  }

  public UserAccount getByUsername(String username) {
    for (UserAccount acc : accounts) {
      if (acc.getUsername().equalsIgnoreCase(username))
        return acc;
    }
    return null;
  }

  // ---------- UPDATE ----------
  public void changePassword(UserAccount user, String newHash) {
    user.setPasswordHash(newHash);
    save();
  }

  public void changeUsername(UserAccount user, String newName) {
    user.setUsername(newName);
    save();
  }

  // ---------- SAVE ----------
  private void save() {
    storage.save(accounts);
  }
}
