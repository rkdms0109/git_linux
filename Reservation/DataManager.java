import java.io.*;
import java.util.*;

// 간단한 CSV 저장/불러오기 전담 클래스 (static 메소드로 활용)
public class DataManager {
    // 저장
    public static <T extends AbstractEntity> void save(String filename, List<T> list) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (T item : list) pw.println(item.toLine());
        }
    }

    // 불러오기
    public static <T extends AbstractEntity> List<T> load(String filename, EntityFactory<T> factory) throws IOException {
        List<T> result = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return result;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                T item = factory.create();
                item.fromLine(line);
                result.add(item);
            }
        }
        return result;
    }

    // 엔티티 객체 생성을 위한 팩토리 인터페이스
    public interface EntityFactory<T> {
        T create();
    }
}
