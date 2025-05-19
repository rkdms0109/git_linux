구조도:

```
공통

- save (영구저장, csv?)

User

- Nickname, password( 4pin?)
- Login, Logout, Register, Unregister(withdraw) 기능

Group

- create, join 기능
- member 확인

Calendar (Schedule)

- U_add, U_delete (개인 일정)
- G_add, G_delete (Team 일정)
- View_week (개인 일정 or Team 일정 or 개인&Team 일정) (y축 시간, x축 요일?)
- (f) View_month(Team 일정 있는 날 확인 → 시간 파악까지.)

Main()

- 위 3개 불러오와서 로직 연결(CLI?창에서 어떻게 보여줄지, 가독성)

추상화 - Abstract - id, toLine(), fromLine() 정의 (id는 객체 구별, line은 csv 저장한 데이터)

상속 - User, Group, Schedule이 위에 거를 extends

다형성 - 독립 작성.

```

구조도를 근거하여 각 영역마다 코드를 작성하고 main.java에서 불러와서 로직연결을 해보자.
중요한점은 객체지향에 입각한 코드 작성이다. 이 코드는 자바 기초 수업에서  객체지향코드로 활용될거 기에 스프링부트, swing, 외부 api등은 금지한다.
