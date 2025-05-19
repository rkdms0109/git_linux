import java.util.*;

public class Main {
    // 데이터 리스트
    static List<User> users = new ArrayList<>();
    static List<Group> groups = new ArrayList<>();
    static List<Schedule> schedules = new ArrayList<>();
    static int userIdSeq = 1001, groupIdSeq = 2001, schedIdSeq = 3001;

    // 세션 상태
    static User currentUser = null;

    public static void main(String[] args) throws Exception {
        // CSV에서 데이터 불러오기
        users = DataManager.load("users.csv", User::new);
        groups = DataManager.load("groups.csv", Group::new);
        schedules = DataManager.load("schedules.csv", Schedule::new);

        Scanner sc = new Scanner(System.in);
        String cmd;
        printHelp();

        while (true) { // 기본 화면 //
            System.out.print("\n명령어 입력 (help:도움말, exit:종료): "); 
            cmd = sc.nextLine().trim();
            if (cmd.equals("exit")) break;
            else if (cmd.equals("help")) printHelp(); // 명령어 도움말 
            else if (cmd.startsWith("register ")) register(cmd); // 회원가입
            else if (cmd.startsWith("login ")) login(cmd);  // 로그인
            else if (cmd.equals("logout")) logout();  // 로그아웃 
            else if (cmd.startsWith("create-group ")) createGroup(cmd);  // 그룹 생성
            else if (cmd.startsWith("join-group ")) joinGroup(cmd);  // 그룹 가입
            else if (cmd.equals("list-groups")) listGroups();  // 그룹목록/멤버 확인
            else if (cmd.startsWith("add-schedule ")) addSchedule(cmd);  // 개인 일정 추가
            else if (cmd.startsWith("add-gschedule ")) addGroupSchedule(cmd); // 그룹 일정 추가
            else if (cmd.startsWith("view-week")) viewWeek(cmd); // 주간 일정 추가
            else if (cmd.startsWith("view-month")) viewMonth(cmd); // 월간 일정 보기
            else if (cmd.startsWith("delete-schedule ")) deleteSchedule(cmd); // 일정 삭제
            else if (cmd.startsWith("recommend-time ")) recommendTime(cmd);// 그룹 공통 시간 추천
            else if (cmd.equals("save")) saveAll(); // 데이터 종료
            else System.out.println("알 수 없는 명령입니다."); // 기본 화면으로 돌아감 
        }
        saveAll(); // 종료시 저장
        System.out.println("프로그램 종료."); // 프로그램 종료 
    }

    // -------------- 각 기능 구현 ----------------

    static void printHelp() { // 명령어 도움말 목록
        System.out.println("\n--- 명령어 목록 ---");
        System.out.println("register 닉네임 4PIN          : 회원가입");
        System.out.println("login 닉네임 4PIN             : 로그인");
        System.out.println("logout                        : 로그아웃");
        System.out.println("create-group 그룹명           : 그룹 생성");
        System.out.println("join-group 그룹명             : 그룹 가입");
        System.out.println("list-groups                   : 그룹 목록/멤버 확인");
        System.out.println("add-schedule 날짜 시간 제목   : 개인 일정 추가(예: add-schedule 2024-06-01 13:00-14:00 시험공부)");
        System.out.println("add-gschedule 그룹명 날짜 시간 제목 : 그룹 일정 추가");
        System.out.println("view-week                     : 주간 일정 보기");
        System.out.println("view-month                    : 월간 일정 보기");
        System.out.println("delete-schedule 일정ID        : 일정 삭제");
        System.out.println("recommend-time 그룹명 날짜(YYYY-MM-DD) : 그룹 공통 시간 추천");
        System.out.println("save                          : 데이터 저장");
        System.out.println("exit                          : 프로그램 종료");
    }

    static void register(String cmd) {
        if (currentUser != null) { System.out.println("로그아웃 후 이용하세요."); return; } //로그인 완료 후 등록 하려고 할 시
        String[] parts = cmd.split(" ");
        if (parts.length != 3) { System.out.println("형식: register 닉네임 4PIN"); return; }
        String nick = parts[1], pin = parts[2];
        for (User u : users) if (u.getNickname().equals(nick)) {
            System.out.println("이미 존재하는 닉네임입니다."); return; // 닉네임 중복 사항 제외
        }
        users.add(new User(userIdSeq++, nick, pin));
        System.out.println("회원가입 완료.");
    }

    static void login(String cmd) {
        if (currentUser != null) { System.out.println("이미 로그인 중."); return; } // 다른 로그인 시도시
        String[] parts = cmd.split(" ");
        if (parts.length != 3) { System.out.println("형식: login 닉네임 4PIN"); return; } // 명령어(register), 이름, pin 의 개수가 맞지 않을 경우
        for (User u : users) if (u.checkLogin(parts[1], parts[2])) {
            currentUser = u;
            System.out.println("로그인 성공. 환영합니다, " + currentUser.getNickname()); // 로그인 성공시
            return;
        }
        System.out.println("로그인 실패. 닉네임/비밀번호 확인."); // register 항목과 일치하지 않을 경우
    }

    static void logout() {
        if (currentUser == null) { System.out.println("로그인 필요."); return; } // 로그인 안하고 로그아웃 시도시 
        currentUser = null;
        System.out.println("로그아웃 완료."); // 로그아웃 성공시 
    }

    static void createGroup(String cmd) {
        if (currentUser == null) { System.out.println("로그인 필요."); return; }  // 로그인 안하고 그룹 생성 시도시
        String[] parts = cmd.split(" ");
        if (parts.length != 2) { System.out.println("형식: create-group 그룹명"); return; } // 형식 (create-group만 작성시 알 수 없는 명령으로 뜸.)
        String gname = parts[1];
        for (Group g : groups) if (g.getGroupName().equals(gname)) {
            System.out.println("이미 존재하는 그룹명."); return;  // 그룹명 동일 시 
        }
        Group g = new Group(groupIdSeq++, gname);
        g.addMember(currentUser.getId());
        groups.add(g);
        System.out.println("그룹 생성 및 자동 가입 완료."); // 그룹 생성 성공 시 
    }

    static void joinGroup(String cmd) {
        if (currentUser == null) { System.out.println("로그인 필요."); return; } // 로그인 안하고 가입 시도시 
        String[] parts = cmd.split(" ");
        if (parts.length != 2) { System.out.println("형식: join-group 그룹명"); return; } // 형식(join-group만 작성시 알수 없음 명령 처리)
        for (Group g : groups) if (g.getGroupName().equals(parts[1])) {                    // 생성된 그룹 이름들 뜨게 만드는 것도 좋은 방법 ..?
            g.addMember(currentUser.getId());
            System.out.println("그룹 가입 완료."); return; // 그룹 가입 두번 시도 시 ..? 이름 중복 되나요요
        }
        System.out.println("그룹이 존재하지 않음."); // 생성된 그룹이 아닐 경우
    }

    static void listGroups() {
        if (groups.isEmpty()) { System.out.println("생성된 그룹이 없습니다."); return; } // 생성된 그룹이 없는 경우
        for (Group g : groups) {
            System.out.print("그룹명: " + g.getGroupName() + " / 멤버: ");  // 로그인 시도 후 확인 완료
            List<String> names = new ArrayList<>();
            for (int uid : g.getMemberIds()) {
                for (User u : users) if (u.getId() == uid) names.add(u.getNickname());
            }
            System.out.println(String.join(", ", names));
        }
    }

    static void addSchedule(String cmd) {
        if (currentUser == null) { System.out.println("로그인 필요."); return; } // 로그인 안하고 개인 일정 추가시
        String[] parts = cmd.split(" ", 4);
        if (parts.length != 4) { System.out.println("형식: add-schedule 날짜 시간 제목"); return; } // 시간 처리 안됨 (30:00으로 해도 오류 안남) -시간 제한 설정 필요요
        String date = parts[1], time = parts[2];
    
        // 기존 내 개인 일정과 겹침 체크  // 본인 일정만
        for (Schedule s : schedules) {
            if (!s.isGroup() && s.getOwnerId() == currentUser.getId() && s.getDate().equals(date)) {
                if (isTimeOverlap(s.getTime(), time)) {
                    System.out.println("이미 해당 시간에 일정이 존재합니다. (ID: " + s.getId() + ", " + s.getTime() + ", " + s.getTitle() + ")");
                    return;
                }
            }
        }
        schedules.add(new Schedule(schedIdSeq++, currentUser.getId(), false, parts[3], date, time));
        System.out.println("개인 일정 추가 완료."); // 본인 일정 추가 완료시
    }
    
    static void addGroupSchedule(String cmd) {
        if (currentUser == null) { System.out.println("로그인 필요."); return; }
        String[] parts = cmd.split(" ", 5);
        if (parts.length != 5) { System.out.println("형식: add-gschedule 그룹명 날짜 시간 제목"); return; }
        String gname = parts[1], date = parts[2], time = parts[3];
    
        for (Group g : groups) if (g.getGroupName().equals(gname)) {
            if (!g.getMemberIds().contains(currentUser.getId())) {
                System.out.println("해당 그룹 멤버가 아님."); return; // 그룹 멤버가 아닌 경우에도 접근이 가능함 ..
            }
            // 그룹 일정 중복 체크(해당 그룹)
            for (Schedule s : schedules) {
                if (s.isGroup() && s.getOwnerId() == g.getId() && s.getDate().equals(date)) {
                    if (isTimeOverlap(s.getTime(), time)) {
                        System.out.println("이미 해당 시간에 그룹 일정이 존재합니다. (ID: " + s.getId() + ", " + s.getTime() + ", " + s.getTitle() + ")");
                        return; // 그룹룹 시간 겹침 경우 
                    }
                }
            }
            schedules.add(new Schedule(schedIdSeq++, g.getId(), true, parts[4], date, time));
            System.out.println("그룹 일정 추가 완료."); return;
        }
        System.out.println("그룹명 확인 필요."); // 그룹 일정 추가 후 만약 개인 일정과 시간이 겹친다면 ..?
        // 3번째 사람 로그인 -> 1, 2번만 들어가 있는 그룹 일정 보임 ...? why -- 
    }
    

    // 주간 뷰
    static void viewWeek(String cmd) {
        if (currentUser == null) { System.out.println("로그인 필요."); return; } // 로그인 안하고 시도시
        // 예시: view-week
        System.out.println("--- 이번주 내/그룹 일정 ---");
        Calendar today = Calendar.getInstance();
        int week = today.get(Calendar.WEEK_OF_YEAR);
        int year = today.get(Calendar.YEAR);

        // 개인 + 소속 그룹 id 수집 
        Set<Integer> relGroupIds = new HashSet<>();
        for (Group g : groups)
            if (g.getMemberIds().contains(currentUser.getId())) relGroupIds.add(g.getId());

        for (Schedule s : schedules) {
            Calendar c = Calendar.getInstance();
            try {
                String[] d = s.getDate().split("-");
                c.set(Integer.parseInt(d[0]), Integer.parseInt(d[1])-1, Integer.parseInt(d[2]));
                if (c.get(Calendar.WEEK_OF_YEAR) == week && c.get(Calendar.YEAR) == year) {
                    // 내 일정 또는 내 그룹 일정 (ID 표시 추가)
                    if (!s.isGroup() && s.getOwnerId() == currentUser.getId())
                        System.out.println("[내일정][ID:" + s.getId() + "] " + s.getDate() + " " + s.getTime() + " " + s.getTitle());
                    else if (s.isGroup() && relGroupIds.contains(s.getOwnerId()))
                        System.out.println("[그룹][ID:" + s.getId() + "] " + groupNameById(s.getOwnerId()) + " " + s.getDate() + " " + s.getTime() + " " + s.getTitle());
                }
            } catch(Exception e) { /* 무시 */ }
        }
    }

    // 월간 뷰
    static void viewMonth(String cmd) {
        if (currentUser == null) { System.out.println("로그인 필요."); return; }
        System.out.println("--- 이번달 내/그룹 일정 (날짜별 개수) ---");
        Calendar today = Calendar.getInstance();
        int month = today.get(Calendar.MONTH) + 1;
        int year = today.get(Calendar.YEAR);

        Map<String, Integer> dateCnt = new TreeMap<>();
        Set<Integer> relGroupIds = new HashSet<>();
        for (Group g : groups)
            if (g.getMemberIds().contains(currentUser.getId())) relGroupIds.add(g.getId());

        for (Schedule s : schedules) {
            String[] d = s.getDate().split("-");
            try {
                int y = Integer.parseInt(d[0]), m = Integer.parseInt(d[1]);
                if (y == year && m == month) {
                    if (!s.isGroup() && s.getOwnerId() == currentUser.getId())
                        dateCnt.put(s.getDate(), dateCnt.getOrDefault(s.getDate(), 0) + 1);
                    else if (s.isGroup() && relGroupIds.contains(s.getOwnerId()))
                        dateCnt.put(s.getDate(), dateCnt.getOrDefault(s.getDate(), 0) + 1);
                }
            } catch(Exception e) {}
        }
        // 월간 뷰에서는 일정 개수만 보여주고, 해당 날짜의 일정 ID를 함께 표시
        for (String dateKey : dateCnt.keySet()) {
            System.out.println(dateKey + ": " + dateCnt.get(dateKey) + "건");
            
            // 해당 날짜의 일정 ID를 보여줌
            System.out.print("   일정 ID: ");
            boolean hasSchedules = false;
            for (Schedule s : schedules) {
                if (s.getDate().equals(dateKey) && ((!s.isGroup() && s.getOwnerId() == currentUser.getId()) || 
                    (s.isGroup() && isMyGroup(s.getOwnerId())))) {
                    System.out.print(s.getId() + "("+s.getTitle()+"), ");
                    hasSchedules = true;
                }
            }
            if (!hasSchedules) System.out.print("없음");
            System.out.println();
        }
    }

    // ID → 그룹명
    static String groupNameById(int id) {
        for (Group g : groups) if (g.getId() == id) return g.getGroupName();
        return "(알수없음)";
    }

    // 데이터 전체 저장
    static void saveAll() {
        try {
            DataManager.save("users.csv", users);
            DataManager.save("groups.csv", groups);
            DataManager.save("schedules.csv", schedules);
            System.out.println("저장 완료.");
        } catch (Exception e) { System.out.println("저장 중 오류: " + e.getMessage()); }
    }

    static void recommendTime(String cmd) {
        if (currentUser == null) { System.out.println("로그인 필요."); return; }
        // 예: recommend-time 그룹명 2024-06-10
        String[] parts = cmd.split(" ");
        if (parts.length != 3) {
            System.out.println("형식: recommend-time 그룹명 날짜(YYYY-MM-DD)"); return;
        }
        String gname = parts[1], date = parts[2];
        Group targetGroup = null;
        for (Group g : groups)
            if (g.getGroupName().equals(gname)) { targetGroup = g; break; }
        if (targetGroup == null) { System.out.println("그룹 없음."); return; }
        List<Integer> memberIds = targetGroup.getMemberIds();
        if (memberIds.isEmpty()) { System.out.println("그룹 멤버 없음."); return; }
    
        // 07~23시 각 멤버별로 가능한 시간 계산
        int startHour = 7, endHour = 23;
        int totalSlots = endHour - startHour + 1;
        // 각 멤버별 [시간슬롯] => true(비어있음)/false(일정있음)
        Map<Integer, boolean[]> memberSlots = new HashMap<>();
        for (int uid : memberIds) {
            boolean[] slots = new boolean[totalSlots];
            Arrays.fill(slots, true);
            for (Schedule s : schedules) {
                if ((!s.isGroup() && s.getOwnerId() == uid) && s.getDate().equals(date)) {
                    try {
                        // 시간 문자열을 표준화된 형식으로 변환
                        String standardTime = standardizeTimeFormat(s.getTime());
                        String[] tt = standardTime.split("-");
                        
                        // 시간 부분 추출
                        int sh, eh;
                        if (tt[0].contains(":")) {
                            sh = Integer.parseInt(tt[0].split(":")[0]);
                        } else {
                            sh = Integer.parseInt(tt[0]);
                        }
                        
                        if (tt[1].contains(":")) {
                            eh = Integer.parseInt(tt[1].split(":")[0]);
                        } else {
                            eh = Integer.parseInt(tt[1]);
                        }
                        
                        for (int h = sh; h < eh; h++) {
                            if (h >= startHour && h <= endHour)
                                slots[h - startHour] = false; // 일정 있는 시간 false
                        }
                    } catch (Exception e) {
                        System.out.println("시간 형식 파싱 오류 (일정 ID:" + s.getId() + ", 시간:" + s.getTime() + "): " + e.getMessage());
                        // 오류가 있는 일정은 건너뜁니다
                    }
                }
            }
            memberSlots.put(uid, slots);
        }
    
        // 각 시간대별 가능한 인원수 집계
        int[] availCnt = new int[totalSlots];
        for (int i = 0; i < totalSlots; i++) {
            int cnt = 0;
            for (boolean[] slots : memberSlots.values())
                if (slots[i]) cnt++;
            availCnt[i] = cnt;
        }
    
        // 1) 모든 인원이 비는 연속 구간 찾기
        int maxSpan = 0, maxStart = -1;
        int curSpan = 0, curStart = -1;
        for (int i = 0; i < totalSlots; i++) {
            if (availCnt[i] == memberIds.size()) {
                if (curSpan == 0) curStart = i;
                curSpan++;
            } else {
                if (curSpan > maxSpan) {
                    maxSpan = curSpan; maxStart = curStart;
                }
                curSpan = 0;
            }
        }
        if (curSpan > maxSpan) { maxSpan = curSpan; maxStart = curStart; }
    
        if (maxSpan > 0) {
            System.out.println("※ " + gname + " 그룹, " + date + "에 '모든 멤버'가 가능한 시간:");
            int sh = startHour + maxStart;
            int eh = sh + maxSpan;
            System.out.printf("   %02d:00~%02d:00 (%d시간 연속)\n", sh, eh, maxSpan);
            return;
        }
    
        // 2) 최대 인원이 가장 길게 비는 연속 시간대 찾기
        // 슬라이딩 윈도우: 최대 연속, 최대 인원
        int bestCnt = 0, bestSpan = 0, bestStart = -1, bestEnd = -1;
        for (int people = memberIds.size() - 1; people >= 1; people--) {
            curSpan = 0; curStart = -1;
            for (int i = 0; i < totalSlots; i++) {
                if (availCnt[i] >= people) {
                    if (curSpan == 0) curStart = i;
                    curSpan++;
                    if (curSpan > bestSpan || (curSpan == bestSpan && people > bestCnt)) {
                        bestSpan = curSpan; bestCnt = people; bestStart = curStart; bestEnd = i;
                    }
                } else {
                    curSpan = 0;
                }
            }
            if (bestSpan > 0) break; // 가장 많은 인원으로 가장 긴 구간 찾으면 바로 종료
        }
        if (bestSpan > 0) {
            System.out.println("※ 모든 멤버가 동시에 비는 시간 없음.");
            System.out.println("※ 가장 많은 인원이, 가장 오래 비는 시간대 추천:");
            int sh = startHour + bestStart;
            int eh = sh + bestSpan;
            System.out.printf("   %02d:00~%02d:00 (%d시간, 최대 %d명)\n", sh, eh, bestSpan, bestCnt);
            // 참여 가능 멤버
            Set<Integer> candidateIds = new HashSet<>();
            for (int uid : memberIds) {
                boolean allFree = true;
                for (int t = bestStart; t <= bestEnd; t++)
                    if (!memberSlots.get(uid)[t]) { allFree = false; break; }
                if (allFree) candidateIds.add(uid);
            }
            System.out.print("   참여 가능 멤버: ");
            List<String> nicks = new ArrayList<>();
            for (int uid : candidateIds)
                for (User u : users) if (u.getId() == uid) nicks.add(u.getNickname());
            System.out.println(String.join(", ", nicks));
        } else {
            System.out.println("추천 가능한 공통 시간대가 없습니다.");
        }
    }

    // 두 시간구간(HH:mm-HH:mm)이 겹치는지 판별
    static boolean isTimeOverlap(String t1, String t2) {
        String[] p1 = t1.split("-");
        String[] p2 = t2.split("-");
        int s1 = Integer.parseInt(p1[0].replace(":", ""));
        int e1 = Integer.parseInt(p1[1].replace(":", ""));
        int s2 = Integer.parseInt(p2[0].replace(":", ""));
        int e2 = Integer.parseInt(p2[1].replace(":", ""));
        return s1 < e2 && s2 < e1;
    }

    // 명령어: delete-schedule 일정ID
    static void deleteSchedule(String cmd) {
        if (currentUser == null) { System.out.println("로그인 필요."); return; }
        String[] parts = cmd.split(" ");
        if (parts.length != 2) { System.out.println("형식: delete-schedule 일정ID"); return; }
        int delId;
        try { delId = Integer.parseInt(parts[1]); }
        catch (Exception e) { System.out.println("일정ID는 숫자입니다."); return; }
        Iterator<Schedule> it = schedules.iterator();
        while (it.hasNext()) {
            Schedule s = it.next();
            // 본인 개인일정 또는 자신이 속한 그룹의 일정만 삭제 허용
            if (( !s.isGroup() && s.getOwnerId() == currentUser.getId() && s.getId() == delId )
             || ( s.isGroup() && s.getId() == delId && isMyGroup(s.getOwnerId()))) {
                it.remove();
                System.out.println("일정(ID:" + delId + ") 삭제 완료.");
                return;
            }
        }
        System.out.println("삭제 권한이 없거나 일정이 존재하지 않습니다.");
    }
    // 자신의 그룹 소속 여부 판별
    static boolean isMyGroup(int groupId) {
        for (Group g : groups)
            if (g.getId() == groupId && g.getMemberIds().contains(currentUser.getId()))
                return true;
        return false;
    }
    
    // 시간 형식 표준화 (다양한 입력 형식을 "HH:00-HH:00" 와 같은 형태로 변환)
    // 시간 오류야.. 제발 나지좀 마렴.
    static String standardizeTimeFormat(String timeInput) {
        if (timeInput == null || timeInput.trim().isEmpty()) {
            return "00:00-00:00"; // 기본값
        }
        
        try {
            String time = timeInput.trim();
            String startTime, endTime;
            
            if (time.contains("-")) {
                // 하이픈이 있는 경우 (시작-종료 형태)
                String[] parts = time.split("\\s*-\\s*"); // 공백 포함 분리
                startTime = parts[0].trim();
                endTime = parts[1].trim();
            } else {
                // 하이픈이 없는 경우 (단일 시간) - 1시간 길이로 가정
                startTime = time;
                
                // 시간 추출
                int hour;
                if (startTime.contains(":")) {
                    hour = Integer.parseInt(startTime.split(":")[0]);
                } else {
                    hour = Integer.parseInt(startTime);
                }
                
                // 종료 시간은 시작 + 1시간
                endTime = (hour + 1) + ":00";
            }
            
            // 최종 형식화: 콜론이 없는 경우 추가
            if (!startTime.contains(":")) {
                startTime = startTime + ":00";
            }
            if (!endTime.contains(":")) {
                endTime = endTime + ":00";
            }
            
            return startTime + "-" + endTime;
            
        } catch (Exception e) {
            System.out.println("시간 표준화 오류: " + e.getMessage());
            return timeInput; // 오류 발생 시 원본 반환
        }
    }
}