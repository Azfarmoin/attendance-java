package models;

/**
 * Attendance record - tracks student attendance per session
 */
public class Attendance {
    private String id;
    private String studentId;
    private String studentName;
    private String rollNumber;
    private String date;       // "2024-01-15"
    private String subject;
    private String status;     // "Present", "Absent", "Late"
    private String markedBy;   // user name
    private String className;

    public Attendance() {}

    public Attendance(String id, String studentId, String studentName, String rollNumber,
                      String date, String subject, String status, String markedBy, String className) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.date = date;
        this.subject = subject;
        this.status = status;
        this.markedBy = markedBy;
        this.className = className;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMarkedBy() { return markedBy; }
    public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    // Helper methods
    public String getStatusColor() {
        return switch (status) {
            case "Present" -> "#a8f000";
            case "Absent"  -> "#ff3d4a";
            case "Late"    -> "#ffcc50";
            default        -> "#5a5a7a";
        };
    }

    public String getStatusBg() {
        return switch (status) {
            case "Present" -> "rgba(168,240,0,0.12)";
            case "Absent"  -> "rgba(255,61,74,0.12)";
            case "Late"    -> "rgba(255,204,80,0.12)";
            default        -> "rgba(90,90,122,0.12)";
        };
    }
}
