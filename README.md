# ⚡ AttendX — Futuristic Student Management System
### A vibrant, modern JavaFX desktop application

---

## 🎨 What Is This?

AttendX is a **fully offline, standalone JavaFX application** that feels like a Gen-Z SaaS dashboard. Inspired by Spotify, Discord, Arc Browser, and modern gaming launchers — built entirely with beginner-friendly Java code.

---

## 🚀 Quick Start (Run the Pre-Built JAR)

### Prerequisites
- Java 11+ with JavaFX (OpenJDK 11 + OpenJFX recommended)

### On Linux / macOS:
```bash
java \
  --module-path /usr/share/openjfx/lib \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
  -jar AttendX.jar
```

### On Windows (with JavaFX SDK installed):
```cmd
java --module-path "C:\javafx-sdk\lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
     -jar AttendX.jar
```

### Easy launcher script (Linux/macOS):
```bash
chmod +x run.sh
./run.sh
```

---

## 🔑 Demo Login Credentials

| Role    | Email                  | Password    |
|---------|------------------------|-------------|
| Admin   | admin@school.com       | admin123    |
| Teacher | sarah@school.com       | teacher123  |

Use the **Quick Login buttons** on the login screen for instant access.

---

## 🏗️ Build From Source

### Requirements
- JDK 21
- OpenJFX 11+ (installed via `apt install openjfx` on Ubuntu)
- json-simple JAR (included in `lib/`)

### Compile & Run:
```bash
./run.sh
```

Or manually:
```bash
javac \
  --module-path /usr/share/openjfx/lib \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
  -cp lib/json-simple.jar \
  -d out \
  $(find src -name "*.java")

java \
  --module-path /usr/share/openjfx/lib \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
  -cp "out:lib/json-simple.jar" \
  app.Main
```

---

## 📁 Project Structure

```
AttendX/
├── src/
│   ├── app/           → Main.java (entry point)
│   ├── ui/            → LoginView.java, MainView.java (app shell)
│   ├── pages/         → All 8 page screens
│   │   ├── DashboardPage.java
│   │   ├── StudentsPage.java
│   │   ├── AttendancePage.java
│   │   ├── GradesPage.java
│   │   ├── AnalyticsPage.java
│   │   ├── SubjectsPage.java
│   │   ├── TimetablePage.java
│   │   └── ProfilePage.java
│   ├── components/    → Reusable UI components (StudentFormDialog)
│   ├── models/        → Data models (User, Student, Attendance, Grade, Subject)
│   ├── services/      → DataService.java, SessionService.java
│   ├── utils/         → UIUtils.java (all styling helpers)
│   └── styles/        → dark.css (global JavaFX CSS)
├── lib/
│   └── json-simple.jar
├── AttendX.jar        → Pre-built runnable JAR
├── run.sh             → Build & run script
└── README.md
```

---

## ✨ Features

### 🔐 Authentication
- Role-based login (Admin / Teacher)
- Quick-login buttons
- Local JSON user database
- Session management

### 📊 Dashboard
- Real-time attendance stats
- 7-day attendance bar chart
- Today's summary pie chart
- Top performers leaderboard
- Recent activity feed
- Live counter animations

### 👤 Students
- Full CRUD (Add / Edit / Delete)
- Search by name, roll number, email
- Filter by class and section
- Gender-coded avatars
- CSV export

### 📋 Attendance
- Mark attendance with Present / Absent / Late buttons
- Mark-all controls per class
- Live summary counts
- Date + subject + class session control
- Full history with filters
- CSV export

### 🎓 Grades
- Grade overview with letter grades (A+ → F)
- Assessment breakdown (Quiz / Midterm / Final / Project)
- GPA calculation
- Clickable grade detail modal
- Pass/fail status badges
- CSV export

### 📈 Analytics
- 30-day attendance trend line chart
- Grade distribution pie chart
- Per-class attendance bar chart
- Subject-wise average score bars
- Student performance table with at-risk detection

### 📚 Subjects
- Subject cards with color coding
- Credit hours and grade record counts

### ⏰ Timetable
- Full 5-day weekly schedule grid
- Color-coded by subject
- Current day highlight
- Break period display

### 👑 Profile
- User info display
- System overview stats
- Java / OS info
- Manual save trigger
- Profile edit form

---

## 💾 Data Storage

All data is stored locally at: `~/.attendx/data/`

Files:
- `users.json` — login credentials
- `students.json` — student records
- `attendance.json` — attendance history
- `grades.json` — grade records

Data is **auto-seeded** on first run with 12 students, 30 days of attendance, and grade records across 3 subjects.

---

## 🎨 Design System

| Token            | Value           |
|------------------|-----------------|
| Background dark  | `#08080f`       |
| Card             | `#0e0e1a`       |
| Accent green     | `#a8f000`       |
| Accent blue      | `#60aaff`       |
| Accent purple    | `#b48aff`       |
| Accent pink      | `#ff8aaa`       |
| Accent yellow    | `#ffcc50`       |
| Accent red       | `#ff3d4a`       |
| Accent cyan      | `#40d8e8`       |
| Text primary     | `#f0f0f8`       |
| Text muted       | `#3d3d5a`       |

---

## 🛠️ Tech Stack

| Layer       | Technology              |
|-------------|-------------------------|
| UI          | JavaFX 11+              |
| Styling     | JavaFX CSS              |
| Charts      | JavaFX Charts API       |
| JSON        | json-simple 2.3.0       |
| Storage     | Local JSON files        |
| Auth        | In-memory session       |
| Build       | javac + bash script     |

---

## 📦 Adding JavaFX on Different Systems

**Ubuntu / Debian:**
```bash
sudo apt install openjfx
```

**Windows:**
1. Download JavaFX SDK from https://openjfx.io
2. Extract to `C:\javafx-sdk`
3. Update the `--module-path` in your run command

**macOS (with Homebrew):**
```bash
brew install openjfx
```

---

## 🧩 Extending the App

The code is deliberately beginner-friendly. To add a new page:

1. Create `src/pages/MyPage.java` with a `getRoot()` method
2. Add it to the switch in `ui/MainView.java → navigateTo()`
3. Add a nav item in the `NAV_ITEMS` array in `MainView.java`

---

*Built with ❤️ using pure JavaFX — no backend, no Node.js, no Spring Boot.*
