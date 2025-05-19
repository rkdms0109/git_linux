public class User extends AbstractEntity{
	private String nickname;
    private String password; // 4자리 숫자

    // 생성자
    public User() {}
    public User(int id, String nickname, String password) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
    }

    // CSV 저장용 변환
    @Override
    public String toLine() {
        return id + "," + nickname + "," + password;
    }

    // CSV 불러오기용 변환
    @Override
    public void fromLine(String line) {
        String[] parts = line.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.nickname = parts[1];
        this.password = parts[2];
    }

    // Getter, Setter
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // 로그인 체크
    public boolean checkLogin(String nickname, String password) {
        return this.nickname.equals(nickname) && this.password.equals(password);
    } 
}
