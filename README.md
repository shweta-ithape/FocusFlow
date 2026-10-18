# FocusFlow — Smart Study Session Planner 🎓⚡

FocusFlow is a full-stack student productivity and smart study session planner built with **Spring Boot 3 (Java 17/21)**, **Angular**, and **MySQL**. It uses a rule-based **greedy scheduling algorithm** to prioritize study topics based on exam deadlines, topic difficulty, and subject importance.

Designed specifically to be beginner-friendly, readable, and easy to explain during **Fresher Software Developer Interviews and College Viva Examinations**.

---

## 🌟 Key Features

1. **Modern Productivity Dashboard**: Inspired by clean student dashboards with real-time KPI metrics (Planned Minutes, Completed Sessions, Remaining Tasks, Topic Coverage %).
2. **Subject & Course Management**: Add, view, edit, and delete academic courses with exam dates and importance levels (`LOW`, `MEDIUM`, `HIGH`).
3. **Topic Breakdown**: Organize topics under subjects with study durations and difficulty levels (`EASY`, `MEDIUM`, `HARD`).
4. **Greedy Study Schedule Generator**:
   - Computes weighted priority scores based on exam urgency, subject importance, and topic difficulty.
   - Automatically allocates daily study hours without exceeding the user's limit.
   - Intelligently splits large topics into manageable study blocks.
   - Prevents duplicate schedules when regenerating for the same date.
5. **Study Session Tracker**: Log actual minutes studied, mark sessions as `COMPLETED` or `INCOMPLETE`.
6. **Built-in Pomodoro Focus Timer**: 25-minute deep focus interval, 5-minute short break, 15-minute long break, and audio chime notification using the browser Web Audio API.
7. **Weekly Progress Analytics**: Pure HTML/CSS progress bars showing study time distribution across subjects.

---

## 🏗️ Architecture & Folder Structure

FocusFlow follows a clean 3-tier layered architecture:

```
[ Angular Single Page App ]  (Port 4200)
            │  HTTP REST Calls / JSON
            ▼
[ Spring Boot REST Controllers ] (Port 8080)
            │
[ Business Service Layer & Greedy Engine ]
            │
[ Spring Data JPA & Hibernate ]
            │
[ MySQL / H2 Database ]
```

```text
FocusFlow - SpringBoot Angular Project/
├── backend/                              # Spring Boot Backend
│   ├── pom.xml                           # Maven Dependencies
│   └── src/
│       ├── main/
│       │   ├── java/com/focusflow/
│       │   │   ├── FocusFlowApplication.java
│       │   │   ├── config/               # Web CORS & Data Seeder
│       │   │   │   ├── CorsConfig.java
│       │   │   │   └── DataInitializer.java
│       │   │   ├── controller/           # REST Endpoints
│       │   │   │   ├── SubjectController.java
│       │   │   │   ├── TopicController.java
│       │   │   │   ├── ScheduleController.java
│       │   │   │   └── ProgressController.java
│       │   │   ├── dto/                  # Data Transfer Objects
│       │   │   │   ├── ScheduleRequest.java
│       │   │   │   ├── SessionStatusUpdateRequest.java
│       │   │   │   ├── DashboardStatsDto.java
│       │   │   │   ├── SubjectProgressDto.java
│       │   │   │   └── WeeklyProgressDto.java
│       │   │   ├── entity/               # JPA Entities & Enums
│       │   │   │   ├── Subject.java
│       │   │   │   ├── Topic.java
│       │   │   │   ├── StudySession.java
│       │   │   │   ├── Importance.java
│       │   │   │   ├── Difficulty.java
│       │   │   │   └── SessionStatus.java
│       │   │   ├── exception/            # Global Exception Handling
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   ├── BadRequestException.java
│       │   │   │   └── GlobalExceptionHandler.java
│       │   │   ├── repository/           # Spring Data JPA Repositories
│       │   │   │   ├── SubjectRepository.java
│       │   │   │   ├── TopicRepository.java
│       │   │   │   └── StudySessionRepository.java
│       │   │   └── service/              # Core Business Logic & Algorithms
│       │   │       ├── SubjectService.java
│       │   │       ├── TopicService.java
│       │   │       ├── ScheduleService.java  <-- GREEDY SCHEDULER
│       │   │       └── ProgressService.java
│       │   └── resources/
│       │       ├── application.properties     # MySQL configuration
│       │       ├── application-h2.properties  # In-memory dev configuration
│       │       └── mysql-schema.sql           # SQL script for manual setup
│       └── test/
│           └── java/com/focusflow/service/ScheduleServiceTest.java
│
├── frontend/                             # Angular Frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   ├── navbar/               # Header Navigation
│   │   │   │   ├── dashboard/            # KPI cards & Quick Generator
│   │   │   │   ├── subjects/             # Subject CRUD
│   │   │   │   ├── topics/               # Topic CRUD
│   │   │   │   ├── schedule/             # Daily Session Tracker
│   │   │   │   ├── pomodoro/             # 25-minute Pomodoro Clock
│   │   │   │   └── progress/             # Weekly analytics & charts
│   │   │   ├── models/                   # TypeScript interfaces
│   │   │   ├── services/                 # Angular HttpClient API services
│   │   │   ├── app.routes.ts             # Client-side routing
│   │   │   └── app.config.ts
│   │   └── styles.css                    # Modern CSS (blue/purple palette)
│   └── package.json
└── README.md
```

---

## ⚙️ Technology Stack

- **Frontend**: Angular 19+, TypeScript, HTML5, Modern CSS3, Angular Router, Angular HttpClient.
- **Backend**: Java 17/21, Spring Boot 3.3.4, Spring Web MVC, Spring Data JPA, Hibernate ORM, Maven.
- **Database**: MySQL 8.0 (with automatic table generation via `ddl-auto=update`) or zero-config H2.
- **Testing**: JUnit 5, Mockito.

---

## 🧮 The Greedy Scheduling Algorithm (Viva Explanation)

Located in `ScheduleService.java`:

### 1. The Priority Score Formula
$$\text{Priority Score} = (\text{Urgency Score} \times 3) + (\text{Importance Score} \times 2) + \text{Difficulty Score}$$

- **Urgency Score** (Days until exam):
  - $\le 3 \text{ days} \implies 10 \text{ pts}$ (Immediate urgency)
  - $\le 7 \text{ days} \implies 7 \text{ pts}$ (Within one week)
  - $\le 14 \text{ days} \implies 5 \text{ pts}$ (Within two weeks)
  - $\le 30 \text{ days} \implies 3 \text{ pts}$ (Within one month)
  - $> 30 \text{ days} \implies 1 \text{ pt}$
  - Passed exam $\implies$ Skipped safely
- **Importance Score**: `HIGH = 3`, `MEDIUM = 2`, `LOW = 1`
- **Difficulty Score**: `HARD = 3`, `MEDIUM = 2`, `EASY = 1`

### 2. Time Allocation Steps
1. Filter incomplete topics (`completed == false`) whose exam date is in the future.
2. Sort topics in descending order of Priority Score.
3. Allocate available minutes greedily.
4. If a topic requires more time than remaining daily capacity, the algorithm **splits** the topic (allocates whatever time is left) and stops.
5. Daily limit is never exceeded.

---

## 🚀 How to Run the Application

### Option A: Running with MySQL (Default)

1. **Start MySQL Service**: Ensure MySQL is running on port 3306.
2. **Set Credentials**:
   Open `backend/src/main/resources/application.properties` and verify your username and password:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
   *(Or set the environment variable: `$env:DB_PASSWORD="YOUR_PASSWORD"`)*
3. **Run Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   *(Spring Boot will automatically create the database `focusflow_db` and all tables!)*

### Option B: Running with H2 In-Memory (Zero Setup / Instant Offline Demo)

If you do not have MySQL configured or want an instant demo:
```bash
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

---

### Running Frontend (Angular)

1. Open a new terminal:
   ```bash
   cd frontend
   npm start
   ```
   *(Or `npx ng serve`)*
2. Open your browser and navigate to:
   ```
   http://localhost:4200
   ```

---

## 📡 REST API Catalog

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `GET` | `/api/subjects` | Get all subjects & nested topics | None |
| `POST` | `/api/subjects` | Add new subject | `{"name": "OS", "examDate": "2026-10-25", "importance": "HIGH"}` |
| `PUT` | `/api/subjects/{id}` | Update existing subject | Updated Subject JSON |
| `DELETE`| `/api/subjects/{id}` | Delete subject (cascade deletes topics) | None |
| `GET` | `/api/topics` | Get all topics (optional `?subjectId=1`) | None |
| `POST` | `/api/topics` | Add new topic | `{"name": "Threads", "difficulty": "MEDIUM", "estimatedMinutes": 60, "subject": {"id": 1}}` |
| `PATCH`| `/api/topics/{id}/complete` | Toggle topic completion | None |
| `POST` | `/api/schedule/generate` | Auto-generate daily study plan | `{"date": "2026-10-14", "availableHours": 2.5}` |
| `GET` | `/api/schedule?date=YYYY-MM-DD` | Get sessions for a date | None |
| `PATCH`| `/api/schedule/sessions/{id}/status` | Update session status & actual time | `{"status": "COMPLETED", "actualMinutes": 90}` |
| `GET` | `/api/progress/dashboard` | Dashboard KPI summary statistics | None |
| `GET` | `/api/progress/weekly` | Weekly study time & subject breakdown | None |

---

## 🎯 Viva & Interview Q&A Cheatsheet

### 1. What problem does FocusFlow solve?
> FocusFlow removes the cognitive load of deciding what to study each day. By applying a weighted greedy scheduling algorithm to factors like exam urgency, topic difficulty, and subject importance, it outputs an optimal, realistic daily study roadmap tailored to the student's available hours.

### 2. Why did you use a Greedy Algorithm?
> A greedy algorithm makes the locally optimal choice at each step (highest priority score first). It operates in $O(N \log N)$ time complexity due to sorting, which is fast, deterministic, and easy to explain compared to heavy dynamic programming or search algorithms.

### 3. How did you prevent Infinite JSON recursion in bidirectional JPA relationships?
> In `Subject.java` we annotate `@JsonIgnoreProperties("subject")` on the `topics` list, and in `Topic.java` we annotate `@JsonIgnoreProperties("topics")` on the `Subject` field. This prevents Jackson from getting stuck in an infinite serialization loop between Parent and Child entities.

### 4. What is CORS and how did you configure it?
> Cross-Origin Resource Sharing (CORS) is a browser security mechanism that blocks requests made to a different origin (port 4200 to port 8080). We enabled it globally using a `WebMvcConfigurer` bean in `CorsConfig.java` allowing `http://localhost:4200` to send `GET`, `POST`, `PUT`, `PATCH`, and `DELETE` requests.

### 5. Why use Angular Standalone Components?
> Standalone components eliminate the need for `NgModule`, making components modular, lightweight, and directly declaring their dependencies in the `imports: []` array.
