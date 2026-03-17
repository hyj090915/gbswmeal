# GBSWMeal — 경북소프트웨어마이스터고 급식 평가 앱
> Claude Code 개발 설명서 v1.0

## 프로젝트 기본 정보

| 항목 | 값 |
|---|---|
| 교육청 코드 (ATPT_OFCDC_SC_CODE) | `R10` |
| 학교 코드 (SD_SCHUL_CODE) | `8750829` |
| 허용 이메일 도메인 | `@sc.gyo6.net` |
| 백엔드 | Spring Boot 3.2.x + Java 17 |
| 프론트엔드 | React 18 + TypeScript (Vite) |
| 스타일링 | CSS Modules |
| 데이터베이스 | MySQL 8.0 (Docker) |
| 인증 방식 | JWT (Spring Security 6.x) |
| 배포 | 로컬 개발 우선 |

---

## 핵심 기능 (우선순위 순)

1. **급식 메뉴 확인** — 나이스 API 자동 수집, 오늘/주간 조회
2. **좋아요/싫어요 투표** — 메뉴별 반응, 싫어요 이유 선택, 1인 1회 제한
3. **학교 이메일 로그인** — @sc.gyo6.net 도메인만 허용, JWT 인증
4. **댓글/평가 작성** — 로그인 유저만 가능, 별점(1~5점) + 텍스트
5. **다음달 메뉴 제안 투표** — 학생이 메뉴 제안 + 공감 투표 (선택 기능)

---

## 프로젝트 폴더 구조

```
gbswmeal/
├── backend/
│   ├── src/main/java/com/gbsw/meal/
│   │   ├── GbswMealApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtConfig.java
│   │   │   └── SchedulerConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── MealController.java
│   │   │   ├── VoteController.java
│   │   │   ├── CommentController.java
│   │   │   └── SuggestionController.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── MealService.java
│   │   │   ├── NeisApiService.java
│   │   │   ├── VoteService.java
│   │   │   ├── CommentService.java
│   │   │   └── SuggestionService.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── MealRepository.java
│   │   │   ├── VoteRepository.java
│   │   │   ├── CommentRepository.java
│   │   │   └── SuggestionRepository.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Meal.java
│   │   │   ├── Vote.java
│   │   │   ├── Comment.java
│   │   │   └── MenuSuggestion.java
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── JwtAuthFilter.java
│   │   │   └── CustomUserDetails.java
│   │   └── scheduler/
│   │       └── MealFetchScheduler.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── build.gradle
├── frontend/
│   ├── src/
│   │   ├── api/index.ts
│   │   ├── components/
│   │   │   ├── MealCard/
│   │   │   ├── VoteButton/
│   │   │   ├── CommentList/
│   │   │   └── SuggestionBoard/
│   │   ├── pages/
│   │   │   ├── LoginPage/
│   │   │   ├── RegisterPage/
│   │   │   ├── TodayMealPage/
│   │   │   ├── WeeklyMealPage/
│   │   │   └── SuggestionPage/
│   │   ├── hooks/
│   │   │   ├── useAuth.ts
│   │   │   └── useMeal.ts
│   │   ├── types/index.ts
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── package.json
│   └── vite.config.ts
├── docker-compose.yml
└── CLAUDE.md  ← 이 파일
```

---

## 데이터베이스 설계

### 테이블 목록

| 테이블 | 설명 | 주요 컬럼 |
|---|---|---|
| `users` | 학생 계정 | id, email, password, role, created_at |
| `meals` | 급식 데이터 | id, meal_date, meal_type, dish_names, cal_info |
| `votes` | 좋아요/싫어요 | id, meal_id, user_id, vote_type, dislike_reason |
| `comments` | 급식 평가 댓글 | id, meal_id, user_id, content, rating, created_at |
| `menu_suggestions` | 메뉴 제안 | id, title, description, user_id, vote_count, deadline |
| `suggestion_votes` | 제안 공감 | id, suggestion_id, user_id, created_at |

### User Entity

```java
@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;           // 반드시 @sc.gyo6.net 도메인만 허용

    @Column(nullable = false)
    private String password;        // BCrypt 암호화

    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;  // STUDENT, ADMIN

    private LocalDateTime createdAt;
}
```

### Meal Entity

```java
@Entity @Table(name = "meals")
public class Meal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate mealDate;

    private String mealType;        // 조식/중식/석식

    @Column(columnDefinition = "TEXT")
    private String dishNames;       // 메뉴 이름 (\n 구분)

    private String calInfo;
    private String ntrInfo;
    private LocalDateTime createdAt;

    @Transient private int likeCount;
    @Transient private int dislikeCount;
}
```

### Vote Entity

```java
@Entity
@Table(name = "votes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"meal_id", "user_id"}))
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "meal_id")
    private Meal meal;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;      // LIKE, DISLIKE

    private String dislikeReason;   // SALTY, SPICY, TASTELESS, COLD, PORTION_SMALL, OTHER

    private LocalDateTime createdAt;
}
```

---

## REST API 설계

### 인증 API

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| POST | `/api/auth/register` | X | 회원가입 (이메일 도메인 검증) |
| POST | `/api/auth/login` | X | 로그인 → JWT 반환 |
| GET | `/api/auth/me` | O | 내 정보 조회 |

### 급식 API

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/meals/today` | X | 오늘 급식 조회 |
| GET | `/api/meals/week` | X | 이번 주 급식 조회 |
| GET | `/api/meals/{date}` | X | 특정 날짜 (YYYYMMDD) |
| POST | `/api/meals/fetch` | ADMIN | 나이스 API 수동 수집 |

### 투표 API

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| POST | `/api/votes` | O | 투표 등록 또는 변경 |
| DELETE | `/api/votes/{mealId}` | O | 투표 취소 |
| GET | `/api/votes/{mealId}/result` | X | 투표 결과 조회 |
| GET | `/api/votes/{mealId}/mine` | O | 내 투표 조회 |

### 댓글 API

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/comments/{mealId}` | X | 댓글 목록 |
| POST | `/api/comments/{mealId}` | O | 댓글 작성 |
| PUT | `/api/comments/{commentId}` | O | 내 댓글 수정 |
| DELETE | `/api/comments/{commentId}` | O | 내 댓글 삭제 |

### 메뉴 제안 API (선택 기능)

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/suggestions` | X | 제안 목록 (공감순) |
| POST | `/api/suggestions` | O | 제안 등록 |
| POST | `/api/suggestions/{id}/vote` | O | 공감 투표 |
| DELETE | `/api/suggestions/{id}/vote` | O | 공감 취소 |

---

## 핵심 구현 가이드

### 나이스 API 연동 (NeisApiService)

```
GET https://open.neis.go.kr/hub/mealServiceDietInfo
  ?Type=json
  &ATPT_OFCDC_SC_CODE=R10
  &SD_SCHUL_CODE=8750829
  &MLSV_YMD=20260317
```

응답 파싱:
- `mealServiceDietInfo[1].row[0].DDISH_NM` — 메뉴 목록 (`<br/>` → `\n` replace)
- `mealServiceDietInfo[1].row[0].MMEAL_SC_NM` — 조식/중식/석식
- `mealServiceDietInfo[1].row[0].CAL_INFO` — 칼로리
- 데이터 없는 날(방학 등)은 `mealServiceDietInfo[0].head[0].list_total_count === 0` 으로 판단

### 스케줄러

```java
@Component
public class MealFetchScheduler {
    // 평일 오전 7시 자동 수집
    @Scheduled(cron = "0 0 7 * * MON-FRI")
    public void fetchTodayMeal() {
        neisApiService.fetchAndSaveMeal(LocalDate.now());
    }

    // 매주 일요일 오후 6시 — 다음주 데이터 미리 수집
    @Scheduled(cron = "0 0 18 * * SUN")
    public void fetchWeeklyMeals() {
        LocalDate nextMonday = LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(1);
        for (int i = 0; i < 5; i++) {
            neisApiService.fetchAndSaveMeal(nextMonday.plusDays(i));
        }
    }
}
```

### 이메일 도메인 검증

```java
private static final String ALLOWED_DOMAIN = "sc.gyo6.net";

public void register(RegisterRequest request) {
    if (!request.getEmail().endsWith("@" + ALLOWED_DOMAIN)) {
        throw new IllegalArgumentException("학교 이메일(@sc.gyo6.net)만 가입 가능합니다.");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new IllegalArgumentException("이미 가입된 이메일입니다.");
    }
    // ... BCrypt 암호화 후 저장
}
```

### 투표 중복 방지 로직

```java
public void vote(Long mealId, VoteRequest request, Long userId) {
    Optional<Vote> existing = voteRepository.findByMealIdAndUserId(mealId, userId);
    if (existing.isPresent()) {
        Vote vote = existing.get();
        if (vote.getVoteType() == request.getVoteType()) {
            voteRepository.delete(vote);   // 같은 타입 → 취소
        } else {
            vote.setVoteType(request.getVoteType());
            vote.setDislikeReason(request.getDislikeReason());
            voteRepository.save(vote);     // 다른 타입 → 변경
        }
    } else {
        voteRepository.save(/* 새 Vote 생성 */);
    }
}
```

### 싫어요 이유 Enum

```java
public enum DislikeReason {
    SALTY,          // 너무 짜요
    SPICY,          // 너무 매워요
    TASTELESS,      // 맛이 없어요
    COLD,           // 식었어요
    PORTION_SMALL,  // 양이 너무 적어요
    OTHER           // 기타
}
```

---

## 환경 설정 파일

### docker-compose.yml

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: gbswmeal-db
    environment:
      MYSQL_ROOT_PASSWORD: root1234
      MYSQL_DATABASE: gbswmeal
      MYSQL_USER: gbswmeal_user
      MYSQL_PASSWORD: gbswmeal1234
    ports:
      - "3306:3306"
    volumes:
      - gbswmeal-mysql-data:/var/lib/mysql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
volumes:
  gbswmeal-mysql-data:
```

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gbswmeal?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: gbswmeal_user
    password: gbswmeal1234
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
  scheduling:
    enabled: true

jwt:
  secret: gbswmeal-secret-key-must-be-at-least-256-bits-long-for-hs256
  expiration: 86400000

neis:
  api:
    base-url: https://open.neis.go.kr/hub
    atpt-code: R10
    school-code: 8750829

server:
  port: 8080
```

### build.gradle 주요 의존성

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
    implementation 'com.mysql:mysql-connector-j'
    implementation 'org.jsoup:jsoup:1.17.2'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### CORS 설정 (SecurityConfig.java)

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

---

## 프론트엔드 구현 가이드

### API 클라이언트 (src/api/index.ts)

```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);
```

### TypeScript 타입 (src/types/index.ts)

```typescript
export interface Meal {
  id: number;
  mealDate: string;
  mealType: string;
  dishNames: string;   // \n 구분 메뉴 목록
  calInfo: string;
  likeCount: number;
  dislikeCount: number;
}

export interface Vote {
  mealId: number;
  voteType: 'LIKE' | 'DISLIKE';
  dislikeReason?: 'SALTY' | 'SPICY' | 'TASTELESS' | 'COLD' | 'PORTION_SMALL' | 'OTHER';
}

export interface Comment {
  id: number;
  mealId: number;
  userEmail: string;
  content: string;
  rating: number;      // 1~5
  createdAt: string;
}

export interface MenuSuggestion {
  id: number;
  title: string;
  description: string;
  userEmail: string;
  voteCount: number;
  deadline: string;
  myVote: boolean;
}
```

### 페이지 목록

| 페이지 | 경로 | 설명 |
|---|---|---|
| LoginPage | `/login` | 이메일/비밀번호 로그인 폼 |
| RegisterPage | `/register` | 회원가입 (@sc.gyo6.net만 허용) |
| TodayMealPage | `/` | 오늘 급식 카드 + 투표 + 댓글 |
| WeeklyMealPage | `/week` | 이번 주 급식 달력 뷰 |
| SuggestionPage | `/suggestions` | 메뉴 제안 + 공감 투표 |

---

## 개발 순서 (권장)

1. `docker-compose.yml` 작성 → `docker compose up -d` 실행
2. Spring Boot 프로젝트 생성 (IntelliJ 내부, Gradle, Java 17)
3. `application.yml`, `build.gradle` 의존성 설정
4. Entity 5개 작성 (User, Meal, Vote, Comment, MenuSuggestion)
5. Repository 인터페이스 작성
6. `NeisApiService` 작성 + 오늘 급식 수동 호출 테스트
7. `MealFetchScheduler` 작성 (@Scheduled)
8. `MealController` + `MealService` 작성
9. JWT 인증 구현 (JwtTokenProvider, JwtAuthFilter, SecurityConfig)
10. `AuthController` + `AuthService` (회원가입, 로그인)
11. `VoteController` + `VoteService` (투표, 중복 방지)
12. `CommentController` + `CommentService` (댓글 CRUD)
13. React 프로젝트 생성 (`npm create vite@latest frontend -- --template react-ts`)
14. API 클라이언트 + 타입 정의
15. LoginPage, RegisterPage
16. TodayMealPage (급식 카드 + 투표 버튼)
17. CommentList 컴포넌트
18. WeeklyMealPage
19. SuggestionPage (선택)
20. 전체 통합 테스트

---

## 주의사항

- **Hibernate dialect**: `application.yml`에 `dialect: org.hibernate.dialect.MySQLDialect` 명시 필수
- **나이스 API 호출 제한**: API Key 없이 하루 100회. 개발 중 반복 테스트 주의
- **DDISH_NM 파싱**: `<br/>` 태그를 반드시 `\n`으로 replace 후 저장
- **투표 UNIQUE 제약**: votes 테이블 `(meal_id, user_id)` UNIQUE 제약 필수
- **JWT secret 길이**: 256비트(32자) 이상 필수. 짧으면 서버 시작 오류
- **DB 연결 타이밍**: MySQL 컨테이너 완전 시작 후 Spring Boot 실행
- **ddl-auto**: 개발 초기는 `create`, 이후 `update`로 변경
