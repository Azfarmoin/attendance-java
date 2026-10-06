package services;

import models.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;

/**
 * DataService - Central data management
 * Handles all CRUD operations and JSON persistence
 */
public class DataService {
    private static DataService instance;

    // In-memory data stores
    private List<User> users = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Attendance> attendances = new ArrayList<>();
    private List<Grade> grades = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();

    // Data directory
    private final String DATA_DIR = System.getProperty("user.home") + "/.attendx/data/";

    private DataService() {}

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    public void initialize() {
        // Create data directory
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }

        // Try to load existing data, otherwise seed
        if (!loadData()) {
            seedData();
            saveAll();
        }
    }

    // ─── SEED DATA ──────────────────────────────────────────────────────────

    private void seedData() {
        System.out.println("Seeding fresh data...");

        // Users
        users.add(new User("u1", "Admin User",     "admin@school.com",  "admin123",   "admin"));
        users.add(new User("u2", "Ms. Sarah Khan", "sarah@school.com",  "teacher123", "teacher"));
        users.add(new User("u3", "Mr. Ali Raza",   "ali@school.com",    "teacher123", "teacher"));

        // Subjects
        subjects.add(new Subject("s1", "Mathematics",      "MATH", 3));
        subjects.add(new Subject("s2", "Physics",          "PHY",  3));
        subjects.add(new Subject("s3", "Chemistry",        "CHEM", 3));
        subjects.add(new Subject("s4", "Biology",          "BIO",  3));
        subjects.add(new Subject("s5", "English",          "ENG",  3));
        subjects.add(new Subject("s6", "Urdu",             "URD",  2));
        subjects.add(new Subject("s7", "Computer Science", "CS",   3));

        // Students
        students.add(new Student("st1",  "Ahmed Hassan",  "10A-001", "10", "A", "male",   "ahmed@student.com",  "03001234567"));
        students.add(new Student("st2",  "Fatima Zahra",  "10A-002", "10", "A", "female", "fatima@student.com", "03001234568"));
        students.add(new Student("st3",  "Omar Sheikh",   "10A-003", "10", "A", "male",   "omar@student.com",   "03001234569"));
        students.add(new Student("st4",  "Ayesha Malik",  "10B-001", "10", "B", "female", "ayesha@student.com", "03001234570"));
        students.add(new Student("st5",  "Bilal Ahmad",   "10B-002", "10", "B", "male",   "bilal@student.com",  "03001234571"));
        students.add(new Student("st6",  "Hassan Ali",    "11A-001", "11", "A", "male",   "hassan@student.com", "03001234573"));
        students.add(new Student("st7",  "Mariam Iqbal",  "11A-002", "11", "A", "female", "mariam@student.com", "03001234574"));
        students.add(new Student("st8",  "Usman Tariq",   "11B-001", "11", "B", "male",   "usman@student.com",  "03001234575"));
        students.add(new Student("st9",  "Noor Fatima",   "12A-001", "12", "A", "female", "noor@student.com",   "03001234576"));
        students.add(new Student("st10", "Imran Butt",    "12A-002", "12", "A", "male",   "imran@student.com",  "03001234579"));
        students.add(new Student("st11", "Zara Ahmed",    "11B-002", "11", "B", "female", "zara@student.com",   "03001234580"));
        students.add(new Student("st12", "Kamran Shah",   "12A-003", "12", "A", "male",   "kamran@student.com", "03001234581"));

        // Set parent info
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            s.setParentName("Parent of " + s.getFirstName());
            s.setParentContact("0300123456" + i);
            s.setDateOfBirth("200" + (8 - i % 3) + "-0" + (i % 9 + 1) + "-15");
            s.setCreatedAt(LocalDate.now().minusDays(i * 5).toString());
        }

        // Attendance - generate 30 days of history
        generateAttendanceData();

        // Grades
        generateGradeData();

        System.out.println("Seed complete: " + students.size() + " students, " +
            attendances.size() + " attendance records, " + grades.size() + " grades");
    }

    private void generateAttendanceData() {
        String[] subjectNames = {"Mathematics", "Physics", "English"};
        String[] statuses = {"Present", "Present", "Present", "Present", "Late", "Absent", "Present"};
        int attId = 1;

        LocalDate today = LocalDate.now();
        for (int d = 0; d < 30; d++) {
            LocalDate date = today.minusDays(d);
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) continue;

            for (String subject : subjectNames) {
                for (int i = 0; i < students.size(); i++) {
                    Student s = students.get(i);
                    String status = statuses[(i + d) % statuses.length];
                    attendances.add(new Attendance(
                        "att" + attId++, s.getId(), s.getName(), s.getRollNumber(),
                        date.toString(), subject, status, "Ms. Sarah Khan", s.getClassName()
                    ));
                }
            }
        }
    }

    private void generateGradeData() {
        String[] subjectIds = {"s1", "s2", "s3"};
        String[] subjectNames = {"Mathematics", "Physics", "Chemistry"};
        double[][] scores = {
            {85, 72, 91, 68, 78, 88, 75, 63, 95, 70, 82, 77},
            {78, 65, 88, 72, 81, 74, 69, 58, 90, 66, 79, 85},
            {92, 80, 76, 85, 73, 95, 82, 71, 88, 74, 67, 91}
        };

        int gradeId = 1;
        for (int si = 0; si < subjectIds.length; si++) {
            for (int i = 0; i < students.size(); i++) {
                Student st = students.get(i);
                Grade g = new Grade("g" + gradeId++, st.getId(), st.getName(), st.getRollNumber(),
                    subjectIds[si], subjectNames[si], st.getClassName());

                double baseScore = scores[si][i % scores[si].length];
                g.getAssessments().add(new Grade.Assessment("Quiz",    "Quiz 1",    25, baseScore * 0.25 * 0.9, 20));
                g.getAssessments().add(new Grade.Assessment("Midterm", "Midterm",   50, baseScore * 0.50 * 0.92, 30));
                g.getAssessments().add(new Grade.Assessment("Final",   "Final Exam",100, baseScore,             50));

                g.setAttendancePercentage(78 + (i % 4) * 5);
                g.computeGrade();
                grades.add(g);
            }
        }
    }

    // ─── PERSISTENCE ────────────────────────────────────────────────────────

    public void saveAll() {
        saveUsers();
        saveStudents();
        saveAttendances();
        saveGrades();
    }

    private boolean loadData() {
        try {
            File studentsFile = new File(DATA_DIR + "students.json");
            if (!studentsFile.exists()) return false;

            loadUsers();
            loadStudents();
            loadAttendances();
            loadGrades();
            return !students.isEmpty();
        } catch (Exception e) {
            System.err.println("Load failed, will re-seed: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void saveStudents() {
        JSONArray arr = new JSONArray();
        for (Student s : students) {
            JSONObject o = new JSONObject();
            o.put("id", s.getId());
            o.put("name", s.getName());
            o.put("rollNumber", s.getRollNumber());
            o.put("className", s.getClassName());
            o.put("section", s.getSection());
            o.put("gender", s.getGender());
            o.put("email", s.getEmail());
            o.put("contact", s.getContact());
            o.put("parentName", s.getParentName());
            o.put("parentContact", s.getParentContact());
            o.put("dateOfBirth", s.getDateOfBirth());
            o.put("isActive", s.isActive());
            o.put("createdAt", s.getCreatedAt());
            arr.add(o);
        }
        writeJson(DATA_DIR + "students.json", arr.toJSONString());
    }

    @SuppressWarnings("unchecked")
    private void loadStudents() throws Exception {
        String json = readJson(DATA_DIR + "students.json");
        if (json == null) return;
        JSONParser p = new JSONParser();
        JSONArray arr = (JSONArray) p.parse(json);
        students.clear();
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            Student s = new Student();
            s.setId(str(o, "id"));
            s.setName(str(o, "name"));
            s.setRollNumber(str(o, "rollNumber"));
            s.setClassName(str(o, "className"));
            s.setSection(str(o, "section"));
            s.setGender(str(o, "gender"));
            s.setEmail(str(o, "email"));
            s.setContact(str(o, "contact"));
            s.setParentName(str(o, "parentName"));
            s.setParentContact(str(o, "parentContact"));
            s.setDateOfBirth(str(o, "dateOfBirth"));
            s.setActive(o.get("isActive") == null || (Boolean)o.get("isActive"));
            s.setCreatedAt(str(o, "createdAt"));
            students.add(s);
        }
    }

    @SuppressWarnings("unchecked")
    private void saveUsers() {
        JSONArray arr = new JSONArray();
        for (User u : users) {
            JSONObject o = new JSONObject();
            o.put("id", u.getId());
            o.put("name", u.getName());
            o.put("email", u.getEmail());
            o.put("password", u.getPassword());
            o.put("role", u.getRole());
            arr.add(o);
        }
        writeJson(DATA_DIR + "users.json", arr.toJSONString());
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() throws Exception {
        String json = readJson(DATA_DIR + "users.json");
        if (json == null) return;
        JSONParser p = new JSONParser();
        JSONArray arr = (JSONArray) p.parse(json);
        users.clear();
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            users.add(new User(str(o,"id"), str(o,"name"), str(o,"email"), str(o,"password"), str(o,"role")));
        }
    }

    @SuppressWarnings("unchecked")
    private void saveAttendances() {
        JSONArray arr = new JSONArray();
        for (Attendance a : attendances) {
            JSONObject o = new JSONObject();
            o.put("id", a.getId());
            o.put("studentId", a.getStudentId());
            o.put("studentName", a.getStudentName());
            o.put("rollNumber", a.getRollNumber());
            o.put("date", a.getDate());
            o.put("subject", a.getSubject());
            o.put("status", a.getStatus());
            o.put("markedBy", a.getMarkedBy());
            o.put("className", a.getClassName());
            arr.add(o);
        }
        writeJson(DATA_DIR + "attendance.json", arr.toJSONString());
    }

    @SuppressWarnings("unchecked")
    private void loadAttendances() throws Exception {
        String json = readJson(DATA_DIR + "attendance.json");
        if (json == null) return;
        JSONParser p = new JSONParser();
        JSONArray arr = (JSONArray) p.parse(json);
        attendances.clear();
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            Attendance a = new Attendance();
            a.setId(str(o,"id")); a.setStudentId(str(o,"studentId"));
            a.setStudentName(str(o,"studentName")); a.setRollNumber(str(o,"rollNumber"));
            a.setDate(str(o,"date")); a.setSubject(str(o,"subject"));
            a.setStatus(str(o,"status")); a.setMarkedBy(str(o,"markedBy"));
            a.setClassName(str(o,"className"));
            attendances.add(a);
        }
    }

    @SuppressWarnings("unchecked")
    private void saveGrades() {
        JSONArray arr = new JSONArray();
        for (Grade g : grades) {
            JSONObject o = new JSONObject();
            o.put("id", g.getId()); o.put("studentId", g.getStudentId());
            o.put("studentName", g.getStudentName()); o.put("rollNumber", g.getRollNumber());
            o.put("subjectId", g.getSubjectId()); o.put("subjectName", g.getSubjectName());
            o.put("term", g.getTerm()); o.put("className", g.getClassName());
            o.put("letterGrade", g.getLetterGrade());
            o.put("finalPercentage", g.getFinalPercentage());
            o.put("gpaPoints", g.getGpaPoints());
            o.put("isPassed", g.isPassed());
            JSONArray asmts = new JSONArray();
            for (Grade.Assessment a : g.getAssessments()) {
                JSONObject ao = new JSONObject();
                ao.put("type", a.getType()); ao.put("title", a.getTitle());
                ao.put("totalMarks", a.getTotalMarks()); ao.put("obtainedMarks", a.getObtainedMarks());
                ao.put("weightage", a.getWeightage());
                asmts.add(ao);
            }
            o.put("assessments", asmts);
            arr.add(o);
        }
        writeJson(DATA_DIR + "grades.json", arr.toJSONString());
    }

    @SuppressWarnings("unchecked")
    private void loadGrades() throws Exception {
        String json = readJson(DATA_DIR + "grades.json");
        if (json == null) return;
        JSONParser p = new JSONParser();
        JSONArray arr = (JSONArray) p.parse(json);
        grades.clear();
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            Grade g = new Grade(str(o,"id"), str(o,"studentId"), str(o,"studentName"),
                str(o,"rollNumber"), str(o,"subjectId"), str(o,"subjectName"), str(o,"className"));
            g.setTerm(str(o,"term")); g.setLetterGrade(str(o,"letterGrade"));
            if (o.get("finalPercentage") != null) g.setFinalPercentage(((Number)o.get("finalPercentage")).doubleValue());
            if (o.get("gpaPoints") != null) g.setGpaPoints(((Number)o.get("gpaPoints")).doubleValue());
            if (o.get("isPassed") != null) g.setPassed((Boolean)o.get("isPassed"));
            JSONArray asmts = (JSONArray) o.get("assessments");
            if (asmts != null) {
                for (Object ao : asmts) {
                    JSONObject a = (JSONObject) ao;
                    Grade.Assessment asmt = new Grade.Assessment();
                    asmt.setType(str(a,"type")); asmt.setTitle(str(a,"title"));
                    if (a.get("totalMarks") != null) asmt.setTotalMarks(((Number)a.get("totalMarks")).doubleValue());
                    if (a.get("obtainedMarks") != null) asmt.setObtainedMarks(((Number)a.get("obtainedMarks")).doubleValue());
                    if (a.get("weightage") != null) asmt.setWeightage(((Number)a.get("weightage")).doubleValue());
                    g.getAssessments().add(asmt);
                }
            }
            grades.add(g);
        }
    }

    private void writeJson(String path, String content) {
        try (PrintWriter w = new PrintWriter(new FileWriter(path))) {
            w.print(content);
        } catch (IOException e) {
            System.err.println("Save error: " + e.getMessage());
        }
    }

    private String readJson(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            return new String(Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            return null;
        }
    }

    private String str(JSONObject o, String key) {
        Object v = o.get(key);
        return v != null ? v.toString() : null;
    }

    // ─── USER OPERATIONS ────────────────────────────────────────────────────

    public User authenticate(String email, String password) {
        return users.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
            .findFirst().orElse(null);
    }

    public List<User> getUsers() { return new ArrayList<>(users); }

    // ─── STUDENT OPERATIONS ─────────────────────────────────────────────────

    public List<Student> getStudents() { return new ArrayList<>(students); }

    public List<Student> searchStudents(String query, String className, String section) {
        return students.stream()
            .filter(s -> {
                if (query != null && !query.isEmpty()) {
                    String q = query.toLowerCase();
                    if (!s.getName().toLowerCase().contains(q) &&
                        !s.getRollNumber().toLowerCase().contains(q) &&
                        !s.getEmail().toLowerCase().contains(q)) return false;
                }
                if (className != null && !className.isEmpty() && !s.getClassName().equals(className)) return false;
                if (section != null && !section.isEmpty() && !s.getSection().equals(section)) return false;
                return true;
            })
            .collect(Collectors.toList());
    }

    public Student getStudentById(String id) {
        return students.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    public void addStudent(Student student) {
        student.setId("st" + (System.currentTimeMillis() % 100000));
        student.setCreatedAt(LocalDate.now().toString());
        students.add(student);
        saveStudents();
    }

    public void updateStudent(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(student.getId())) {
                students.set(i, student);
                break;
            }
        }
        saveStudents();
    }

    public void deleteStudent(String id) {
        students.removeIf(s -> s.getId().equals(id));
        attendances.removeIf(a -> a.getStudentId().equals(id));
        grades.removeIf(g -> g.getStudentId().equals(id));
        saveStudents();
        saveAttendances();
        saveGrades();
    }

    public List<String> getClasses() {
        return students.stream().map(Student::getClassName).distinct().sorted().collect(Collectors.toList());
    }

    // ─── ATTENDANCE OPERATIONS ──────────────────────────────────────────────

    public List<Attendance> getAttendances() { return new ArrayList<>(attendances); }

    public List<Attendance> getAttendances(String date, String status, String className) {
        return attendances.stream()
            .filter(a -> {
                if (date != null && !date.isEmpty() && !a.getDate().equals(date)) return false;
                if (status != null && !status.isEmpty() && !a.getStatus().equals(status)) return false;
                if (className != null && !className.isEmpty() && !a.getClassName().equals(className)) return false;
                return true;
            })
            .sorted(Comparator.comparing(Attendance::getDate).reversed())
            .collect(Collectors.toList());
    }

    public void markAttendance(String date, String subject, String className,
                                Map<String, String> statusMap, String markedBy) {
        // Remove existing records for same date+subject+class
        attendances.removeIf(a -> a.getDate().equals(date) && a.getSubject().equals(subject)
            && a.getClassName().equals(className));

        // Add new records
        for (Map.Entry<String, String> entry : statusMap.entrySet()) {
            Student st = getStudentById(entry.getKey());
            if (st == null) continue;
            Attendance a = new Attendance(
                "att" + System.currentTimeMillis() + "_" + entry.getKey(),
                st.getId(), st.getName(), st.getRollNumber(),
                date, subject, entry.getValue(), markedBy, st.getClassName()
            );
            attendances.add(a);
        }
        saveAttendances();
    }

    public Map<String, Object> getDashboardStats() {
        String today = LocalDate.now().toString();
        List<Attendance> todayRec = attendances.stream()
            .filter(a -> a.getDate().equals(today)).collect(Collectors.toList());

        long todayPresent = todayRec.stream().filter(a -> "Present".equals(a.getStatus())).count();
        long todayAbsent  = todayRec.stream().filter(a -> "Absent".equals(a.getStatus())).count();
        long todayLate    = todayRec.stream().filter(a -> "Late".equals(a.getStatus())).count();

        // Weekly trend (last 7 days)
        Map<String, Map<String,Long>> weekTrend = new LinkedHashMap<>();
        for (int d = 6; d >= 0; d--) {
            String dt = LocalDate.now().minusDays(d).toString();
            Map<String,Long> counts = new HashMap<>();
            List<Attendance> dayRec = attendances.stream().filter(a -> a.getDate().equals(dt)).collect(Collectors.toList());
            counts.put("Present", dayRec.stream().filter(a -> "Present".equals(a.getStatus())).count());
            counts.put("Absent",  dayRec.stream().filter(a -> "Absent".equals(a.getStatus())).count());
            counts.put("Late",    dayRec.stream().filter(a -> "Late".equals(a.getStatus())).count());
            weekTrend.put(dt, counts);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", students.size());
        stats.put("todayPresent",  todayPresent);
        stats.put("todayAbsent",   todayAbsent);
        stats.put("todayLate",     todayLate);
        stats.put("todayTotal",    todayRec.size());
        stats.put("weekTrend",     weekTrend);
        stats.put("subjects",      subjects.size());
        return stats;
    }

    public double getStudentAttendanceRate(String studentId) {
        List<Attendance> recs = attendances.stream()
            .filter(a -> a.getStudentId().equals(studentId)).collect(Collectors.toList());
        if (recs.isEmpty()) return 0;
        long present = recs.stream().filter(a -> "Present".equals(a.getStatus())).count();
        return (present * 100.0) / recs.size();
    }

    // ─── GRADE OPERATIONS ───────────────────────────────────────────────────

    public List<Grade> getGrades() { return new ArrayList<>(grades); }

    public List<Grade> getGradesByStudent(String studentId) {
        return grades.stream().filter(g -> g.getStudentId().equals(studentId)).collect(Collectors.toList());
    }

    public List<Grade> getGradesBySubject(String subjectId) {
        return grades.stream().filter(g -> g.getSubjectId().equals(subjectId)).collect(Collectors.toList());
    }

    public void saveGrade(Grade grade) {
        // Check if exists
        Optional<Grade> existing = grades.stream()
            .filter(g -> g.getStudentId().equals(grade.getStudentId()) &&
                g.getSubjectId().equals(grade.getSubjectId()) &&
                g.getTerm().equals(grade.getTerm()))
            .findFirst();
        if (existing.isPresent()) {
            grades.set(grades.indexOf(existing.get()), grade);
        } else {
            grade.setId("g" + System.currentTimeMillis());
            grades.add(grade);
        }
        saveGrades();
    }

    // ─── SUBJECT OPERATIONS ─────────────────────────────────────────────────

    public List<Subject> getSubjects() { return new ArrayList<>(subjects); }
}
