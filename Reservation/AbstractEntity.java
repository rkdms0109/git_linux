    
public abstract class AbstractEntity {
    
	protected int id;

    // 고유 식별자 반환
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // CSV 한 줄로 변환 (저장용)
    public abstract String toLine();

    // CSV 한 줄에서 객체 생성 (불러오기용)
    public abstract void fromLine(String line);

}
