import java.util.*;

public class Group extends AbstractEntity {
    private String groupName;
    private List<Integer> memberIds = new ArrayList<>();

    public Group() {}

    public Group(int id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    @Override
    public String toLine() {
        // id,groupName,member1|member2|...
        return id + "," + groupName + "," + String.join("|", memberIds.stream().map(String::valueOf).toArray(String[]::new));
    }

    @Override
    public void fromLine(String line) {
        String[] parts = line.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.groupName = parts[1];
        if (parts.length > 2 && !parts[2].isEmpty()) {
            for (String m : parts[2].split("\\|")) memberIds.add(Integer.parseInt(m));
        }
    }

    // 멤버 추가
    public void addMember(int userId) {
        if (!memberIds.contains(userId)) memberIds.add(userId);
    }

    // 멤버 제거
    public void removeMember(int userId) {
        memberIds.remove((Integer)userId);
    }

    public String getGroupName() { return groupName; }
    public List<Integer> getMemberIds() { return memberIds; }
   
}
