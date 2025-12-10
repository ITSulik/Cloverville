package bob.cloverville;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

public class MemberService {

  private final String filePath;
  private final Gson gson;
  private final Map<UUID, Member> members;

  private static final int MAX_POINTS = 50;
  private static final int MAX_NAME_LENGTH = 30;
  private static final Pattern VALID_NAME = Pattern.compile("^[a-zA-Z0-9 ]+$");

  public MemberService(String filePath) {
    this.filePath = filePath;
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    this.members = load();
  }

  // ---------------- LOAD ----------------
  private Map<UUID, Member> load() {
    try (FileReader reader = new FileReader(filePath)) {
      Type type = new TypeToken<Map<UUID, Member>>() {}.getType();
      Map<UUID, Member> data = gson.fromJson(reader, type);
      return (data != null) ? data : new HashMap<>();
    } catch (IOException e) {
      return new HashMap<>();
    }
  }

  // ---------------- SAVE ----------------
  private void save() {
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(members, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // ---------------- VALIDATIONS ----------------
  private void validateMember(Member m) {
    if (m == null) throw new IllegalArgumentException("Member cannot be null");
    validateName(m.getName());

    if (m.getPersonalPoints() < 0)
      throw new IllegalArgumentException("Points cannot be negative");

    if (m.getTotalTasksCompleted() < 0)
      throw new IllegalArgumentException("Total tasks cannot be negative");
  }

  private void validateName(String name) {
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Name cannot be empty");

    if (name.length() > MAX_NAME_LENGTH)
      throw new IllegalArgumentException("Name cannot exceed " + MAX_NAME_LENGTH + " characters");

    if (!VALID_NAME.matcher(name).matches())
      throw new IllegalArgumentException("Name contains invalid characters");
  }

  private String generateUniqueName(String baseName) {
    String name = baseName;
    int suffix = 1;

    Set<String> existing = new HashSet<>();
    for (Member m : members.values()) {
      existing.add(m.getName());
    }

    while (existing.contains(name)) {
      name = baseName + " (" + suffix++ + ")";
    }

    return name;
  }

  // ---------------- CRUD ----------------
  public void addMember(Member m) {
    validateMember(m);
    m.setName(generateUniqueName(m.getName()));
    members.put(m.getId(), m);
    save();
  }

  public void deleteMember(Member m) {
    if (m == null || !members.containsKey(m.getId()))
      throw new IllegalArgumentException("Member not found");

    members.remove(m.getId());
    save();
  }

  public Member getById(UUID id) {
    if (id == null) throw new IllegalArgumentException("ID cannot be null");

    Member m = members.get(id);
    if (m == null) throw new IllegalArgumentException("Member not found");

    return m;
  }

  public List<Member> getAll() {
    List<Member> list = new ArrayList<>(members.values());
    list.sort(Comparator.comparing(Member::getName));
    return list;
  }

  public void updateMember(Member updated) {
    if (updated == null)
      throw new IllegalArgumentException("Member cannot be null");

    Member stored = members.get(updated.getId());
    if (stored == null)
      throw new IllegalArgumentException("Member does not exist");

    validateMember(updated);

    // Only generate a new unique name if the name actually changed
    if (!stored.getName().equals(updated.getName())) {
      stored.setName(generateUniqueName(updated.getName()));
    }

    stored.setPoints(updated.getPersonalPoints());
    stored.setTotalTasksCompleted(updated.getTotalTasksCompleted());

    save();
  }


  public void resetAllPoints() {
    for (Member m : members.values()) {
      m.setPoints(10);
    }
    save();
  }

  // ---------------- BONUS LOGIC ----------------
  private double getBonusPercent(int tasksThisWeek) {
    if (tasksThisWeek <= 1) return 0.30;
    if (tasksThisWeek <= 3) return 0.20;
    if (tasksThisWeek <= 5) return 0.10;
    return 0.0;
  }

  public void applyWeeklyBonusAndReset() {
    for (Member m : members.values()) {
      double bonusPercent = getBonusPercent(m.getTotalTasksCompleted());
      if (bonusPercent > 0) {
        int bonusAmount = (int) Math.round(Math.min(m.getPersonalPoints(), MAX_POINTS) * bonusPercent);
        m.addPoints(bonusAmount);
      }
      m.setTotalTasksCompleted(0);
    }
    save();
  }

  public String getNameById(UUID id)
  {
    Member member = members.get(id);
    if (member != null) {
      return member.getName();
    }
    return "";
  }
}
