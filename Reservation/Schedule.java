public class Schedule extends AbstractEntity {
    private int ownerId;    // User or Group의 id (다형성 활용)
    private boolean isGroup;// true: group 일정, false: 개인
    private String title;
    private String date;    // "yyyy-MM-dd"
    private String time;    // "HH:mm-HH:mm"

    public Schedule() {}

    public Schedule(int id, int ownerId, boolean isGroup, String title, String date, String time) {
        this.id = id;
        this.ownerId = ownerId;
        this.isGroup = isGroup;
        this.title = title;
        this.date = date;
        this.time = time;
    }

    @Override
    public String toLine() {
        // id,ownerId,isGroup,title,date,time
        return id + "," + ownerId + "," + (isGroup ? "1" : "0") + "," + title + "," + date + "," + time;
    }

    @Override
    public void fromLine(String line) {
        String[] parts = line.split(",", -1);
        this.id = Integer.parseInt(parts[0]);
        this.ownerId = Integer.parseInt(parts[1]);
        this.isGroup = "1".equals(parts[2]);
        this.title = parts[3];
        this.date = parts[4];
        this.time = parts[5];
    }

    // Getter, Setter
    public int getOwnerId() { return ownerId; }
    public boolean isGroup() { return isGroup; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
