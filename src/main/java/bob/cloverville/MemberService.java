package bob.cloverville;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemberService {

  private final List<Member> members;
  private final JsonStorage<Member> storage;

  public MemberService(JsonStorage<Member> storage) {
    this.storage = storage;
    this.members = new ArrayList<>(storage.load());
  }

  // ------- CRUD -------
  public void addMember(Member m) {
    members.add(m);
    save();
  }

  public void deleteMember(Member m) {
    members.remove(m);
    save();
  }

  public List<Member> getAll() {
    return new ArrayList<>(members);
  }

  public Member getById(UUID id) {
    for (Member m : members) {
      if (m.getId().equals(id))
        return m;
    }
    return null;
  }

  public ArrayList<Member> getByName(String name) {
    ArrayList<Member> list = new ArrayList<>();
    for (Member m : members) {
      if (m.getName().equalsIgnoreCase(name) || m.getName().contains(name))
        list.add(m);
    }
    return list;
  }
  public void resetAllPoints(int value) {
    for (Member m : members) {
      m.setPoints(value);
    }
    save();
  }


  private void save() {
    storage.save(members);
  }
  public void updateMember(Member m) {
    for (int i = 0; i < members.size(); i++) {
      if (members.get(i).getId().equals(m.getId())) {
        members.set(i, m);
        save();
        return;
      }
    }
    members.add(m);
    save();
  }

}
